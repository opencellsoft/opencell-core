package org.meveo.api;

import org.meveo.api.dto.BaseDto;

public abstract class BaseCrudApi<T extends BaseDto> extends BaseApi implements ApiService<T> {

}
