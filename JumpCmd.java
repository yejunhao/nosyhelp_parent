import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.Collection;

public class JumpCmd implements Command<Void> {

    private String executionId;   // 那个坏掉的儿子 ID (sid-27...)
    private String targetNodeId;  // 目标节点 ID (sid-27...)

    public JumpCmd(String executionId, String targetNodeId) {
        this.executionId = executionId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 1. 获取坏掉的子执行流
        ExecutionEntity childExecution = commandContext.getExecutionEntityManager().findById(executionId);
        
        if (childExecution == null) {
            throw new RuntimeException("运行时表中找不到执行流 (请先清理死信表): " + executionId);
        }

        // 2. 获取父级执行流 (SubProcess)
        ExecutionEntity parentExecution = childExecution.getParent();
        if (parentExecution == null) {
            throw new RuntimeException("严重错误：该节点没有父级，无法执行父级重启策略！ID: " + executionId);
        }

        // 3. 找到目标节点定义
        Process process = ProcessDefinitionUtil.getProcess(parentExecution.getProcessDefinitionId());
        FlowElement targetFlowElement = findFlowElementRecursively(process, targetNodeId);
        
        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点定义未找到: " + targetNodeId);
        }

        System.out.println("正在执行父级重启策略...");

        // 4. 【修正处】删除坏掉的子执行流 (只传两个参数)
        System.out.println("1. 删除损坏的子执行流: " + childExecution.getId());
        commandContext.getExecutionEntityManager()
                .deleteExecutionAndRelatedData(childExecution, "ZOMBIE_RESET");

        // 5. 由父级创建一个全新的子执行流
        System.out.println("2. 父级 (" + parentExecution.getId() + ") 创建新子执行流");
        ExecutionEntity newChildExecution = commandContext.getExecutionEntityManager()
                .createChildExecution(parentExecution);
        
        // 6. 让新儿子指向目标节点
        newChildExecution.setCurrentFlowElement(targetFlowElement);
        newChildExecution.setActive(true);

        // 7. 触发执行
        System.out.println("3. 激活新执行流: " + newChildExecution.getId());
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessOperation(newChildExecution);

        return null;
    }

    // 递归查找工具方法
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
