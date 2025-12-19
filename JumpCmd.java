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

    private String executionId;   // 坏掉的旧儿子 ID
    private String targetNodeId;  // 目标节点 ID

    public JumpCmd(String executionId, String targetNodeId) {
        this.executionId = executionId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        // 1. 获取坏掉的旧儿子
        ExecutionEntity oldChildExecution = executionEntityManager.findById(executionId);
        if (oldChildExecution == null) {
            throw new RuntimeException("找不到旧执行流: " + executionId);
        }

        // 2. 获取父级 (SubProcess)
        ExecutionEntity parentExecution = oldChildExecution.getParent();
        if (parentExecution == null) {
            throw new RuntimeException("父级丢失，无法重启！");
        }

        System.out.println("【启动修复】 父级ID: " + parentExecution.getId());

        // 3. 【第一步】抢救变量 (在删除前备份)
        Map<String, Object> localVariables = oldChildExecution.getVariablesLocal();
        
        // 4. 【第二步】准备目标节点
        Process process = ProcessDefinitionUtil.getProcess(parentExecution.getProcessDefinitionId());
        FlowElement targetFlowElement = findFlowElementRecursively(process, targetNodeId);
        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点定义未找到: " + targetNodeId);
        }

        // =================================================================
        // 🔴 核心变更：先创建新儿子 (防止父级因无子而自动关闭)
        // =================================================================
        System.out.println("1. 创建新执行流 (占位)...");
        ExecutionEntity newChildExecution = executionEntityManager.createChildExecution(parentExecution);
        
        // 5. 【暴力填充】防止任何 NPE 的可能
        if (newChildExecution.getProcessDefinitionId() == null) {
            newChildExecution.setProcessDefinitionId(parentExecution.getProcessDefinitionId());
        }
        if (newChildExecution.getRootProcessInstanceId() == null) {
            newChildExecution.setRootProcessInstanceId(parentExecution.getRootProcessInstanceId());
        }
        if (newChildExecution.getProcessInstanceId() == null) {
            newChildExecution.setProcessInstanceId(parentExecution.getProcessInstanceId());
        }
        if (newChildExecution.getTenantId() == null) {
            newChildExecution.setTenantId(parentExecution.getTenantId());
        }
        // 继承父级的 Scope 属性 (通常 UserTask 不需要是 Scope，但保持默认即可)
        newChildExecution.setActive(true);
        newChildExecution.setScope(false); 

        // 6. 恢复变量
        if (localVariables != null && !localVariables.isEmpty()) {
            newChildExecution.setVariablesLocal(localVariables);
        }

        // 7. 指向目标节点
        newChildExecution.setCurrentFlowElement(targetFlowElement);

        // =================================================================
        // 🔴 核心变更：新儿子站稳后，再杀旧儿子
        // =================================================================
        System.out.println("2. 删除旧执行流: " + oldChildExecution.getId());
        deleteExecutionRecursively(executionEntityManager, oldChildExecution);

        // 8. 触发执行 (最后一步)
        System.out.println("3. 激活新执行流: " + newChildExecution.getId());
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessOperation(newChildExecution);

        return null;
    }

    // 递归删除 (保持不变)
    private void deleteExecutionRecursively(ExecutionEntityManager entityManager, ExecutionEntity execution) {
        // 再次查询以确保拿到最新状态
        List<ExecutionEntity> children = entityManager.findChildExecutionsByParentExecutionId(execution.getId());
        if (children != null) {
            for (ExecutionEntity child : children) {
                deleteExecutionRecursively(entityManager, child);
            }
        }
        execution.removeVariablesLocal(); // 先清变量引用
        entityManager.deleteExecutionAndRelatedData(execution, "ZOMBIE_RESET");
    }

    // 递归查找 (保持不变)
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
