package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.esignature.NatureDocument;

import javax.annotation.Nullable;

@Value.Immutable
@JsonDeserialize(as = ImmutableFilesSignature.class)
public interface FilesSignature {
	
	@Nullable
	String getFilePath();
	@Nullable
	NatureDocument getNature();
	@JsonProperty("parse_anchors")
	boolean getParseAnchors();
}
