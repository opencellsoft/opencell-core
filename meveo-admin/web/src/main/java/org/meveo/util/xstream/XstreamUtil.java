package org.meveo.util.xstream;

import java.io.StringWriter;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.payments.CustomerAccount;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XstreamUtil {
	private static XStream getXStream() {
		XStream xstream = new XStream() {
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@SuppressWarnings("rawtypes")
					@Override
					public boolean shouldSerializeMember(Class definedIn,
							String fieldName) {
						if (fieldName.equals("id")) {
							return true;
						}
						return false;
					}
				};
			}
		};
		xstream.alias("be", BusinessEntity.class);
		xstream.alias("t", Tax.class);
		xstream.alias("s", Seller.class);
		xstream.alias("ot", OfferTemplate.class);
		xstream.alias("ua", UserAccount.class);
		xstream.alias("pp", PricePlanMatrix.class);
		xstream.alias("ba", BillingAccount.class);
		xstream.alias("ca", CustomerAccount.class);
		xstream.alias("os", OneShotChargeTemplate.class);
		xstream.alias("st", ServiceTemplate.class);
		xstream.alias("wt", WalletTemplate.class);
		xstream.alias("su", Subscription.class);
		xstream.alias("rc", RecurringChargeTemplate.class);
		xstream.alias("c", Customer.class);
		xstream.alias("uc", UsageChargeTemplate.class);
		xstream.alias("ed", TriggeredEDRTemplate.class);
		xstream.alias("ct", CounterTemplate.class);
		xstream.alias("ca", Calendar.class);
		xstream.alias("pc", ProviderContact.class);
		xstream.alias("dp", DiscountPlan.class);
		xstream.alias("et", EmailTemplate.class);

		xstream.useAttributeFor(BusinessEntity.class, "id");
		return xstream;
	}

	public static String marshalBusinessEntity(Object o) {
		StringWriter buffer = new StringWriter();
		HierarchicalStreamWriter writer = new CompactWriter(buffer);
		getXStream().marshal(o, writer);
		return buffer.toString();
	}

	public static Object unmarshalBusinessEntity(String msg) {
		return getXStream().fromXML(msg);
	}
}