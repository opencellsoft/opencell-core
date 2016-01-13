package org.meveo.admin.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * @author Edward P. Legaspi
 **/
public abstract class ImageStreamer<T extends BaseEntity> {

	public StreamedContent getImage() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			Long id = Long.parseLong(context.getExternalContext().getRequestParameterMap().get("id"));
			T obj = getPersistenceService().findById(id);
			if (getImageArr(obj) != null) {
				return new DefaultStreamedContent(new ByteArrayInputStream(getImageArr(obj)));
			} else {
				return new DefaultStreamedContent();
			}
		}
	}

	public abstract PersistenceService<T> getPersistenceService();

	public abstract byte[] getImageArr(T obj);

}
