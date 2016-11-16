package org.meveo.admin.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * Show a picture from a rest URI like /meveo/picture/provider/module/tmp/filename.suffix
 * or  /meveo/picture/provider/offerCategory/offerCategoryCode
 * or  /meveo/picture/provider/offer/offerCode
 * or  /meveo/picture/provider/service/serviceCode
 * or  /meveo/picture/provider/product/productCode
 * <p>
 * 3 provider code
 * 4 group : module or offerCategory or offer or service or product
 * 5 tmp, read pictures from tmp folder
 * 6 picture filename like entity's code with suffix png, gif, jpeg, jpg. In case no extension is provided it is assumed as entity's code.
 * </p>
 */
@WebServlet(name = "pictureServlet", urlPatterns = "/picture/*", loadOnStartup = 1000)
public class PictureServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(PictureServlet.class);

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
		String provider = path[3];
		String groupname = path[4];
		try {
			if (path.length == 7 && path[5].equals("tmp")) {
				rootPath = ModuleUtil.getTmpRootPath(provider);
				filename = path[6];
			} else if (path.length == 6) {
				rootPath = ModuleUtil.getPicturePath(provider, groupname);
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
		if (filename.indexOf(".") > 0 || (!groupname.equals("offerCategory") && !groupname.equals("offer") && !groupname.equals("service") && !groupname.equals("product"))) {
			String destfile = rootPath + File.separator + filename;
			data = loadImage(destfile);
		} else {
			Provider providerEntity = providerService.findByCode(provider);
			if (providerEntity == null) {
				log.error("Provider " + provider + " does not exist");
				return;
			}
			if ("offerCategory".equals(groupname)) {
				OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(filename, providerEntity);
				if (offerTemplateCategory == null) {
					log.error("Offer category with code " + filename + " does not exist");
					return;
				}
				
				try {
					data = Files.readAllBytes(Paths.get(rootPath + File.separator + offerTemplateCategory.getImagePath()));
				} catch (IOException e) {
					log.error("Offer category with code " + filename + " does not exist in filesystem");
				}
				
				if (data == null) {
					// load from cached default images
					data = cachedDefaultImages.get(DEFAULT_OFFER_IMAGE);
				}
				
				if (data == null) {
					// load from default images directory
					String imageFile = rootPath + File.separator + DEFAULT_OFFER_CAT_IMAGE;
					data = loadImage(imageFile);
					cachedDefaultImages.put(imageFile, data);
				}
			} else if ("offer".equals(groupname)) {
				ProductOffering offering = productOfferingService.findByCode(filename, providerEntity);
				if (offering == null) {
					log.error("Offer with code " + filename + " does not exist");
					return;
				}
				
				try {
					data = Files.readAllBytes(Paths.get(rootPath + File.separator + offering.getImagePath()));
				} catch (IOException e) {
					log.error("Offer with code " + filename + " does not exist in filesystem");
				}
				
				if (data == null) {
					// load from cached default images
					data = cachedDefaultImages.get(DEFAULT_OFFER_IMAGE);
				}
				if (data == null) {
					// load from default images directory
					String imageFile = rootPath + File.separator + DEFAULT_OFFER_IMAGE;
					data = loadImage(imageFile);
					cachedDefaultImages.put(imageFile, data);
				}
				
			} else if ("service".equals(groupname)) {
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(filename, providerEntity);
				if (serviceTemplate == null) {
					log.error("Service with code " + filename + " does not exist");
					return;
				}

				try {
					data = Files.readAllBytes(Paths.get(rootPath + File.separator + serviceTemplate.getImagePath()));
				} catch (IOException e) {
					log.error("Service with code " + filename + " does not exist in filesystem");
				}
				
				if (data == null) {
					// load from cached default images
					data = cachedDefaultImages.get(DEFAULT_SERVICE_IMAGE);
				}
				if (data == null) {
					// load from default images directory
					String imageFile = rootPath + File.separator + DEFAULT_SERVICE_IMAGE;
					data = loadImage(imageFile);
					cachedDefaultImages.put(imageFile, data);
				}
			} else if ("product".equals(groupname)) {
				ProductTemplate productTemplate = productTemplateService.findByCode(filename, providerEntity);
				if (productTemplate == null) {
					log.error("Product with code " + filename + " does not exist");
					return;
				}

				try {
					data = Files.readAllBytes(Paths.get(rootPath + File.separator + productTemplate.getImagePath()));
				} catch (IOException e) {
					log.error("Product with code " + filename + " does not exist in filesystem");
				}
				
				if (data == null) {
					// load from cached default images
					data = cachedDefaultImages.get(DEFAULT_PRODUCT_IMAGE);
				}
				if (data == null) {
					// load from default images directory
					String imageFile = rootPath + File.separator + DEFAULT_SERVICE_IMAGE;
					data = loadImage(imageFile);
					cachedDefaultImages.put(imageFile, data);
				}
			}
		}
		
		if (data != null) {
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
				log.error("error when read picture , info " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
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
