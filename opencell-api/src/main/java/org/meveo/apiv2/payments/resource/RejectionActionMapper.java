package org.meveo.apiv2.payments.resource;

import static java.util.Optional.ofNullable;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.payments.ImmutableRejectionAction;
import org.meveo.apiv2.payments.RejectionAction;
import org.meveo.model.payments.PaymentRejectionAction;
import org.meveo.model.scripts.ScriptInstance;

public class RejectionActionMapper extends ResourceMapper<RejectionAction, PaymentRejectionAction> {

    @Override
    public RejectionAction toResource(PaymentRejectionAction entity) {
        ImmutableRejectionAction.Builder builder = ImmutableRejectionAction.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .sequence(entity.getSequence());
        if(entity.getScript() != null) {
            builder.scriptInstance(ImmutableResource.builder().code(entity.getScript().getCode())
                    .build());
        }
        return builder.build();
    }

    @Override
    public PaymentRejectionAction toEntity(RejectionAction resource) {
        PaymentRejectionAction rejectionAction = new PaymentRejectionAction();
        rejectionAction.setCode(resource.getCode());
        rejectionAction.setDescription(resource.getDescription());
        ofNullable(resource.getSequence()).ifPresent(rejectionAction::setSequence);
        if(resource.getScriptInstance() != null) {
            ScriptInstance scriptInstance = new ScriptInstance();
            scriptInstance.setId(resource.getScriptInstance().getId());
            scriptInstance.setCode(resource.getScriptInstance().getCode());
            rejectionAction.setScript(scriptInstance);
        }
        return rejectionAction;
    }
}
