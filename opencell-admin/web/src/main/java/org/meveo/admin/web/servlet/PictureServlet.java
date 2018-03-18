package org.meveo.admin.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Show a picture from a rest URI like /meveo/picture/provider/module/tmp/filename.suffix or /meveo/picture/provider/offerCategory/offerCategoryID or
 * /meveo/picture/provider/offer/offerId or /meveo/picture/provider/service/serviceId or /meveo/picture/provider/product/productId
 * <p>
 * 3 provider code 4 group : module or offerCategory or offer or service or product 5 tmp, read pictures from tmp folder 6 picture filename like entity's code with suffix png, gif,
 * jpeg, jpg. In case no extension is provided it is assumed as entity's ID.
 * </p>
 */
@WebServlet(name = "pictureServlet", urlPatterns = "/picture/*", loadOnStartup = 1000)
public class PictureServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(PictureServlet.class);

    public static final String DEFAULT_OFFER_CAT_IMAGE = "offer_cat_default.png";
    public static final String DEFAULT_OFFER_IMAGE = "offer_default.png";
    public static final String DEFAULT_SERVICE_IMAGE = "service_default.png";
    public static final String DEFAULT_PRODUCT_IMAGE = "product_default.png";
    private Map<String, byte[]> cachedDefaultImages = new HashMap<>();

    @Inject
    ProviderService providerService;

    @Inject
    ProductOfferingService productOfferingService;

    @Inject
    ServiceTemplateService serviceTemplateService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        showPicture(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        showPicture(req, resp);
    }

    private void showPicture(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getRequestURI();
        String[] path = url.split("/");
        if (path == null || (path.length < 5)) {
            return;
        }
        String rootPath = null;
        String filename = null;
        String mimeType = null;
        // String provider = path[3];
        String provider = currentUser.getProviderCode();
        String groupname = path[4];
        try {
            if (path.length == 7 && path[5].equals("tmp")) {
                rootPath = ModuleUtil.getTmpRootPath(provider);
                filename = path[6];
            } else if (path.length == 6) {
                rootPath = ModuleUtil.getPicturePath(provider, groupname, false);
                filename = path[5];
            } else {
                log.error("error context path " + url);
                return;
            }
        } catch (Exception e) {
            log.error("error when read picture path. Reason " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            return;
        }

        byte[] data = null;
        String imagePath = null;

        if (filename.indexOf(".") > 0) {
            imagePath = rootPath + File.separator + filename;

        } else {

            Long id = Long.parseLong(filename);

            if ("offerCategory".equals(groupname)) {
                OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findById(id);
                if (offerTemplateCategory == null) {
                    log.error("Offer category with ID " + id + " does not exist");
                    resp.setStatus(HttpStatus.SC_NOT_FOUND);
                    return;
                }

                imagePath = offerTemplateCategory.getImagePath();

            } else if ("offer".equals(groupname)) {
                ProductOffering offering = productOfferingService.findById(id);
                if (offering == null) {
                    log.error("Offer with ID " + id + " does not exist");
                    resp.setStatus(HttpStatus.SC_NOT_FOUND);
                    return;
                }

                imagePath = offering.getImagePath();

            } else if ("service".equals(groupname)) {
                ServiceTemplate serviceTemplate = serviceTemplateService.findById(id);
                if (serviceTemplate == null) {
                    log.error("Service with ID " + id + " does not exist");
                    resp.setStatus(HttpStatus.SC_NOT_FOUND);
                    return;
                }
                imagePath = serviceTemplate.getImagePath();

            } else if ("product".equals(groupname)) {
                ProductTemplate productTemplate = productTemplateService.findById(id);
                if (productTemplate == null) {
                    log.error("Product with ID " + id + " does not exist");
                    resp.setStatus(HttpStatus.SC_NOT_FOUND);
                    return;
                }

                imagePath = productTemplate.getImagePath();

            }
            if (imagePath != null) {
                imagePath = rootPath + File.separator + imagePath;
            }
        }

        if (imagePath != null) {
            data = loadImage(imagePath);
        }

        // Load a default image if not found
        if (data == null) {

            String defaultImage = null;
            if ("offerCategory".equals(groupname)) {
                defaultImage = DEFAULT_OFFER_CAT_IMAGE;
            } else if ("offer".equals(groupname)) {
                defaultImage = DEFAULT_OFFER_IMAGE;
            } else if ("service".equals(groupname)) {
                defaultImage = DEFAULT_SERVICE_IMAGE;
            } else if ("product".equals(groupname)) {
                defaultImage = DEFAULT_PRODUCT_IMAGE;
            } else {
                log.error("Unknown path {}", groupname);
                resp.setStatus(HttpStatus.SC_NOT_FOUND);
                return;
            }

            // load from cached default images
            data = cachedDefaultImages.get(defaultImage);
            imagePath = rootPath + File.separator + defaultImage;
            // load from default images directory
            if (data == null) {
                data = loadImage(imagePath);
                cachedDefaultImages.put(defaultImage, data);
            }
        }

        if (data != null) {
            Path destFile = Paths.get(imagePath);
            try {
                mimeType = Files.probeContentType(destFile);
                if (mimeType != null) {
                    resp.setContentType(mimeType);
                }
            } catch (IOException e) {
                log.error("Failed to determine mime type for {}", destFile, e);
            }

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new ByteArrayInputStream(data);
                out = resp.getOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            } catch (Exception e) {
                log.error("Failed to read picture, info " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
            resp.setStatus(HttpStatus.SC_OK);

        } else {
            resp.setStatus(HttpStatus.SC_NOT_FOUND);
        }
    }

    private byte[] loadImage(String imageFile) {
        log.debug("Loading image: " + imageFile);
        File file = new File(imageFile);
        byte imageByteArray[] = null;
        if (!file.exists()) {
            log.debug("Image file does not exist: " + imageFile);
        }
        try {
            imageByteArray = ModuleUtil.readPicture(imageFile);
        } catch (IOException e) {
            log.error("Error loading image: " + imageFile + " , info " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
        }
        return imageByteArray;
    }
}
