import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.Collection;

public class JumpCmd implements Command<Void> {

    private String executionId;
    private String targetNodeId;

    public JumpCmd(String executionId, String targetNodeId) {
        this.executionId = executionId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntity execution = commandContext.getExecutionEntityManager().findById(executionId);
        if (execution == null) {
            throw new RuntimeException("执行流不存在: " + executionId);
        }

        // 获取流程定义模型
        Process process = ProcessDefinitionUtil.getProcess(execution.getProcessDefinitionId());
        
        // 关键修改：使用递归查找，支持子流程中的节点
        FlowElement targetFlowElement = findFlowElementRecursively(process, targetNodeId);

        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点未找到 (请检查ID是否正确/是否在子流程内): " + targetNodeId);
        }

        // 执行跳转
        execution.setCurrentFlowElement(targetFlowElement);
        execution.setActive(true);
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessOperation(execution);

        return null;
    }

    /**
     * 递归查找节点 (能够穿透 SubProcess)
     */
    private FlowElement findFlowElementRecursively(FlowElementsContainer container, String id) {
        // 1. 先尝试在当前层级找
        FlowElement element = container.getFlowElement(id);
        if (element != null) {
            return element;
        }

        // 2. 如果找不到，遍历所有子元素，看有没有子容器(SubProcess)
        Collection<FlowElement> children = container.getFlowElements();
        for (FlowElement child : children) {
            if (child instanceof FlowElementsContainer) {
                // 3. 递归进入子容器查找
                FlowElement found = findFlowElementRecursively((FlowElementsContainer) child, id);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
