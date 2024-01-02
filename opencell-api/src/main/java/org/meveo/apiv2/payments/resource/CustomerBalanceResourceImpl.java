package org.meveo.apiv2.payments.resource;

import static java.util.Optional.ofNullable;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.payments.CustomerBalance;
import org.meveo.apiv2.report.ImmutableSuccessResponse;
import org.meveo.service.payments.impl.CustomerBalanceService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

@Interceptors({ WsRestApiInterceptor.class })
public class CustomerBalanceResourceImpl implements CustomerBalanceResource {

    @Inject
    private CustomerBalanceService customerBalanceService;

    private final CustomerBalanceMapper mapper = new CustomerBalanceMapper();

    @Override
    public Response create(CustomerBalance resource) {
        org.meveo.model.payments.CustomerBalance customerBalance = mapper.toEntity(resource);
        if(customerBalanceService.findByCode(customerBalance.getCode()) != null) {
            throw new BadRequestException("Customer balance with code "
                    + customerBalance.getCode() + " already exists");
        }
        if (customerBalance.getOccTemplates() == null
                || customerBalance.getOccTemplates().isEmpty()) {
            throw new BadRequestException("Occ templates should not be null or empty");
        }
        try {
            customerBalanceService.create(customerBalance);
        } catch (BusinessException exception) {
            throw new BadRequestException(exception.getMessage());
        }
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Customer balance successfully created\"},\"id\":"
                        + customerBalance.getId() +"} ")
                .build();

    }

    @Override
    public Response update(Long id, CustomerBalance resource) {
        org.meveo.model.payments.CustomerBalance customerBalance = mapper.toEntity(resource);
        customerBalance.setId(id);
        customerBalanceService.update(customerBalance);
        return Response
                .ok(ImmutableSuccessResponse.builder()
                        .status("SUCCESS")
                        .message("Customer balance successfully updated")
                        .build())
                .build();
    }

    @Override
    public Response delete(Long id) {
        org.meveo.model.payments.CustomerBalance customerBalance =
                ofNullable(customerBalanceService.findById(id)).orElseThrow(()
                        -> new NotFoundException("Customer balance does not exist"));
        if(customerBalance.isDefaultBalance()) {
            throw new BadRequestException("Can not remove default customer balance");
        }
        customerBalanceService.remove(customerBalance);
        return Response
                .ok(ImmutableSuccessResponse.builder()
                        .status("SUCCESS")
                        .message("Customer balance successfully deleted")
                        .build())
                .build();
    }
}