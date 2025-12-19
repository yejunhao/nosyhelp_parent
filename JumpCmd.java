import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.List;
import java.util.Map;

public class JumpCmd implements Command<Void> {

    private String executionId;   // 坏掉的儿子 ID
    private String targetNodeId;  // 目标节点 ID (sid-27...)

    public JumpCmd(String executionId, String targetNodeId) {
        this.executionId = executionId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        // 1. 获取坏掉的子执行流
        ExecutionEntity childExecution = executionEntityManager.findById(executionId);
        
        // 如果运行时找不到，尝试去死信作业里找原来的 ExecutionID (这种情况需要你手动处理，这里假设能找到)
        if (childExecution == null) {
            throw new RuntimeException("执行流找不到，请确认ID是否正确: " + executionId);
        }

        ExecutionEntity parentExecution = childExecution.getParent();
        if (parentExecution == null) {
            throw new RuntimeException("父级执行流丢失，无法重启！ID: " + executionId);
        }

        // 2. 【关键新增】抢救遗产：备份局部变量
        Map<String, Object> localVariables = childExecution.getVariablesLocal();
        System.out.println("抢救出的变量: " + localVariables);

        // 3. 找到目标节点定义
        Process process = ProcessDefinitionUtil.getProcess(parentExecution.getProcessDefinitionId());
        FlowElement targetFlowElement = findFlowElementRecursively(process, targetNodeId);
        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点定义未找到: " + targetNodeId);
        }

        // 4. 递归清理门户 (删旧儿子和孙子)
        deleteExecutionRecursively(executionEntityManager, childExecution);

        // 5. 创建新儿子
        ExecutionEntity newChildExecution = executionEntityManager.createChildExecution(parentExecution);
        
        // 6. 【关键新增】继承遗产：恢复局部变量
        if (localVariables != null && !localVariables.isEmpty()) {
            newChildExecution.setVariablesLocal(localVariables);
        }

        // 7. 激活并指向目标
        newChildExecution.setCurrentFlowElement(targetFlowElement);
        newChildExecution.setActive(true);

        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessOperation(newChildExecution);
        
        return null;
    }

    // 递归删除 (保持不变)
    private void deleteExecutionRecursively(ExecutionEntityManager entityManager, ExecutionEntity execution) {
        List<ExecutionEntity> children = entityManager.findChildExecutionsByParentExecutionId(execution.getId());
        if (children != null) {
            for (ExecutionEntity child : children) {
                deleteExecutionRecursively(entityManager, child);
            }
        }
        // 必须先清空变量引用，防止外键报错（某些版本需要）
        execution.removeVariablesLocal(); 
        entityManager.deleteExecutionAndRelatedData(execution, "ZOMBIE_RESET");
    }

    // 查找节点 (保持不变)
    private FlowElement findFlowElementRecursively(FlowElementsContainer container, String id) {
        FlowElement element = container.getFlowElement(id);
        if (element != null) return element;
        for (FlowElement child : container.getFlowElements()) {
            if (child instanceof FlowElementsContainer) {
                FlowElement found = findFlowElementRecursively((FlowElementsContainer) child, id);
                if (found != null) return found;
            }
        }
        return null;
    }
}
