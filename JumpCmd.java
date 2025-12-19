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
            throw new RuntimeException("运行时找不到执行流: " + executionId);
        }

        // 2. 获取父级执行流
        ExecutionEntity parentExecution = childExecution.getParent();
        if (parentExecution == null) {
            throw new RuntimeException("父级执行流丢失: " + executionId);
        }

        // 3. 【抢救变量】(不仅是 Local，建议检查是否需要 Process 级变量，这里先只取 Local)
        Map<String, Object> localVariables = childExecution.getVariablesLocal();
        
        // 4. 找到目标节点定义
        Process process = ProcessDefinitionUtil.getProcess(parentExecution.getProcessDefinitionId());
        FlowElement targetFlowElement = findFlowElementRecursively(process, targetNodeId);
        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点未找到: " + targetNodeId);
        }

        System.out.println("正在执行全量修复...");

        // 5. 递归删除旧数据
        deleteExecutionRecursively(executionEntityManager, childExecution);

        // 6. 【关键】创建新儿子
        ExecutionEntity newChildExecution = executionEntityManager.createChildExecution(parentExecution);
        
        // 7. 【核心修复点】手动补全所有“户口”信息 (防止 NPE)
        // 某些版本的 Activiti 在 createChildExecution 时不会自动透传 Root ID
        if (newChildExecution.getRootProcessInstanceId() == null) {
            newChildExecution.setRootProcessInstanceId(parentExecution.getRootProcessInstanceId());
        }
        if (newChildExecution.getProcessInstanceId() == null) {
            newChildExecution.setProcessInstanceId(parentExecution.getProcessInstanceId());
        }
        // 补全租户ID
        if (newChildExecution.getTenantId() == null) {
            newChildExecution.setTenantId(parentExecution.getTenantId());
        }

        // 8. 恢复变量
        if (localVariables != null && !localVariables.isEmpty()) {
            newChildExecution.setVariablesLocal(localVariables);
        }

        // 9. 指向目标并激活
        newChildExecution.setCurrentFlowElement(targetFlowElement);
        newChildExecution.setActive(true);
        
        // 10. 【双重保险】强制刷新一下实体更新，确保入库
        executionEntityManager.update(newChildExecution);

        System.out.println("新执行流已创建: " + newChildExecution.getId() + 
                           " | RootID: " + newChildExecution.getRootProcessInstanceId());

        // 11. 触发执行
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
        execution.removeVariablesLocal();
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
