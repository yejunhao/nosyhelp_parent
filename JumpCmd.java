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

public class JumpCmd implements Command<Void> {

    private String executionId;   // 坏掉的儿子 ID
    private String targetNodeId;  // 目标节点 ID

    public JumpCmd(String executionId, String targetNodeId) {
        this.executionId = executionId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        // 1. 获取坏掉的子执行流
        ExecutionEntity childExecution = executionEntityManager.findById(executionId);
        if (childExecution == null) {
            throw new RuntimeException("找不到执行流: " + executionId);
        }

        // 2. 获取父级执行流 (SubProcess)
        ExecutionEntity parentExecution = childExecution.getParent();
        if (parentExecution == null) {
            throw new RuntimeException("父级执行流丢失，无法重启！ID: " + executionId);
        }

        // 3. 找到目标节点定义
        Process process = ProcessDefinitionUtil.getProcess(parentExecution.getProcessDefinitionId());
        FlowElement targetFlowElement = findFlowElementRecursively(process, targetNodeId);
        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点定义未找到: " + targetNodeId);
        }

        System.out.println("正在执行递归清理策略...");

        // 4. 【核心修改】递归删除当前节点及其所有子孙节点 (防止外键报错)
        deleteExecutionRecursively(executionEntityManager, childExecution);

        // 5. 由父级创建一个全新的子执行流
        System.out.println("创建新执行流，父级: " + parentExecution.getId());
        ExecutionEntity newChildExecution = executionEntityManager.createChildExecution(parentExecution);
        
        // 6. 让新儿子指向目标节点并激活
        newChildExecution.setCurrentFlowElement(targetFlowElement);
        newChildExecution.setActive(true);

        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessOperation(newChildExecution);

        return null;
    }

    /**
     * 【新增】递归删除执行流及其所有子节点 (解决 act_fk_exe_parent 报错)
     */
    private void deleteExecutionRecursively(ExecutionEntityManager entityManager, ExecutionEntity execution) {
        // A. 先查找当前执行流的所有“儿子”
        List<ExecutionEntity> children = entityManager.findChildExecutionsByParentExecutionId(execution.getId());
        
        // B. 先把“儿子”们都干掉 (递归)
        if (children != null && !children.isEmpty()) {
            for (ExecutionEntity child : children) {
                deleteExecutionRecursively(entityManager, child);
            }
        }
        
        // C. 儿子都没了，现在可以安全地自裁了
        System.out.println("删除执行流: " + execution.getId() + " (节点: " + execution.getActivityId() + ")");
        // 这里的参数 "ZOMBIE_RESET" 是删除原因
        entityManager.deleteExecutionAndRelatedData(execution, "ZOMBIE_RESET");
    }

    // 查找 BPMN 节点的辅助方法 (保持不变)
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
