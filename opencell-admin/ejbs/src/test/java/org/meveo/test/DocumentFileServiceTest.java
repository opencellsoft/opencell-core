package org.meveo.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.document.Document;
import org.meveo.model.document.DocumentCategory;
import org.meveo.service.document.DocumentFileService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Date;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentFileServiceTest {
    @Mock
    private ParamBeanFactory paramBeanFactory;
    @InjectMocks
    private DocumentFileService documentFileService;
    @Mock
    private ParamBean paramBean;
    @Test
    public void calculateRelativePathFromEl(){
        when(paramBeanFactory.getInstance()).thenReturn(paramBean);
        when(paramBean.getProperty("providers.rootDir", "./opencelldata")).thenReturn("./opencelldata");
        when(paramBean.getProperty("provider.rootDir", "default")).thenReturn("default");
        when(paramBean.getProperty("document.rootDir", "documents")).thenReturn("documents");
        when(paramBean.getProperty("document.relativePathEL", "")).thenReturn("#{documentCategory.relativePath}/#{mv:formatDate(document.creationDate,\"yyyy\")}/#{mv:formatDate(document.creationDate,\"MM\")}");
        documentFileService.init();
        DocumentCategory documentCategory = new DocumentCategory();
        documentCategory.setCode("code-pdf");
        documentCategory.setRelativePath("pdf");
        Document document = new Document();
        document.setCreationDate(new Date("2010/06/21"));
        Assert.assertEquals("pdf/2010/06", documentFileService.computeDocumentRelativePath(document, documentCategory));
    }
}
