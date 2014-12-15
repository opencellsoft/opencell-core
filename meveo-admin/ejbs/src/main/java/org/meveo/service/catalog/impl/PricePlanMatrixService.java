/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.RatingService;

@Stateless
public class PricePlanMatrixService extends PersistenceService<PricePlanMatrix> {

	public void create(PricePlanMatrix e) throws BusinessException {
		super.create(e);
		RatingService.setPricePlanDirty();
	}

	public void update(PricePlanMatrix e) {
		super.update(e);
		RatingService.setPricePlanDirty();
	}

	public void remove(Long id) {
		super.remove(id);
		RatingService.setPricePlanDirty();
	}

	public void disable(Long id) {
		super.disable(id);
		RatingService.setPricePlanDirty();
	}

	public void remove(PricePlanMatrix e) {
		super.remove(e);
		RatingService.setPricePlanDirty();
	}

	public void remove(Set<Long> ids) {
		super.remove(ids);
		RatingService.setPricePlanDirty();
	}

	public void update(PricePlanMatrix e, User updater) {
		super.update(e, updater);
		RatingService.setPricePlanDirty();
	}

	public void create(PricePlanMatrix e, User creator) {
		super.create(e, creator);
		RatingService.setPricePlanDirty();
	}

	public void create(PricePlanMatrix e, User creator, Provider provider) {
		super.create(e, creator, provider);
		RatingService.setPricePlanDirty();
	}

	public void removeByPrefix(EntityManager em, String prefix,
			Provider provider) {
		Query query = em
				.createQuery("DELETE PricePlanMatrix m WHERE m.eventCode LIKE '"
						+ prefix + "%' AND m.provider=:provider");
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	public void removeByCode(EntityManager em, String code, Provider provider) {
		Query query = em
				.createQuery("DELETE PricePlanMatrix m WHERE m.eventCode=:code AND m.provider=:provider");
		query.setParameter("code", code);
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	String[] colNames = { "Charge code", "Seller", "Country", "Currency",
			"Start appli.", "End appli.", "Offer code", "Priority",
			"Amount w/out tax", "Amount with tax", "Min quantity",
			"Max quantity", "Criteria 1", "Criteria 2", "Criteria 3",
			"Criteria EL", "Start rating", "End rating", "Min subscr age",
			"Max subscr age" };

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int importFromExcel(EntityManager em, InputStream excelInputStream,
			User user, Provider provider) throws BusinessException {
		int result = 0;
		Workbook workbook;
		ParamBean param = ParamBean.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(param.getProperty(
				"excelImport.dateFormat", "dd/MM/yyyy"));

		// TODO cache entities
		try {
			workbook = Workbook.getWorkbook(excelInputStream);
			Sheet sheet = workbook.getSheet(0);
			int nbRows = sheet.getRows();
			Cell[] headerCells = sheet.getRow(0);
			if (headerCells.length != colNames.length) {
				throw new BusinessException(
						"Invalid number of columns in the excel file.");
			}
			for (int i = 0; i < headerCells.length; i++) {
				if (!colNames[i].equalsIgnoreCase(headerCells[i].getContents())) {
					throw new BusinessException("Invalid column " + i
							+ " found [" + headerCells[i].getContents()
							+ "] but was expecting [" + colNames[i] + "]");
				}
			}
			for (int rowIndex = 1; rowIndex < nbRows; rowIndex++) {
				// TODO : rewrite to alow import of many lines by charge
				Cell[] cells = sheet.getRow(rowIndex);
				PricePlanMatrix pricePlan = null;
				QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "p");
				qb.addCriterion("eventCode", "=", cells[0].getContents(), false);
				qb.addCriterionEntity("provider", provider);
				@SuppressWarnings("unchecked")
				List<PricePlanMatrix> pricePlans = qb.getQuery(em)
						.getResultList();
				if (pricePlans == null || pricePlans.size() == 0) {
					pricePlan = new PricePlanMatrix();
					pricePlan.setProvider(provider);
					pricePlan.setAuditable(new Auditable());
					pricePlan.getAuditable().setCreated(new Date());
					pricePlan.getAuditable().setCreator(user);
					pricePlan.setEventCode(cells[0].getContents());
				} else if (pricePlans.size() == 1) {
					pricePlan = pricePlans.get(0);
				}
				if (pricePlan == null) {
					log.warn("there are several pricePlan records for charge "
							+ cells[0].getContents() + " we do not update them");
				} else {

					int i = 1;
					String sellerCode = cells[i++].getContents();
					String countryCode = cells[i++].getContents();
					String currencyCode = cells[i++].getContents();
					String startSub = cells[i++].getContents();
					String endSub = cells[i++].getContents();
					String offerCode = cells[i++].getContents();
					String priority = cells[i++].getContents();
					String amountWOTax = cells[i++].getContents();
					String amountWithTax = cells[i++].getContents();
					String minQuantity = cells[i++].getContents();
					String maxQuantity = cells[i++].getContents();
					String criteria1 = cells[i++].getContents();
					String criteria2 = cells[i++].getContents();
					String criteria3 = cells[i++].getContents();
					String criteriaEL = cells[i++].getContents();
					String startRating = cells[i++].getContents();
					String endRating = cells[i++].getContents();
					String minSubAge = cells[i++].getContents();
					String maxSubAge = cells[i++].getContents();

					// Seller
					if (!StringUtils.isBlank(sellerCode)) {
						qb = new QueryBuilder(Seller.class, "p");
						qb.addCriterion("code", "=", sellerCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<Seller> sellers = qb.getQuery(em).getResultList();
						Seller seller = null;
						if (sellers == null || sellers.size() == 0) {
							throw new BusinessException(
									"Invalid seller in line " + rowIndex
											+ ", code=" + sellerCode);
						}
						seller = sellers.get(0);
						pricePlan.setSeller(seller);
					} else {
						pricePlan.setSeller(null);
					}

					// Country
					if (!StringUtils.isBlank(countryCode)) {
						qb = new QueryBuilder(TradingCountry.class, "p");
						qb.addCriterion("code", "=", countryCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<TradingCountry> countries = qb.getQuery(em)
								.getResultList();
						TradingCountry tradingCountry = null;
						if (countries == null || countries.size() == 0) {
							throw new BusinessException(
									"Invalid country in line " + rowIndex
											+ ", code=" + countryCode);
						}
						tradingCountry = countries.get(0);
						pricePlan.setTradingCountry(tradingCountry);
					} else {
						pricePlan.setTradingCountry(null);
					}

					// Currency
					if (!StringUtils.isBlank(currencyCode)) {
						qb = new QueryBuilder(TradingCurrency.class, "p");
						qb.addCriterion("code", "=", currencyCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<TradingCurrency> currencies = qb.getQuery(em)
								.getResultList();
						TradingCurrency tradingCurrency = null;
						if (currencies == null || currencies.size() == 0) {
							throw new BusinessException(
									"Invalid currency in line " + rowIndex
											+ ", code=" + countryCode);
						}
						tradingCurrency = currencies.get(0);
						pricePlan.setTradingCurrency(tradingCurrency);
					} else {
						pricePlan.setTradingCurrency(null);
					}

					// startAppli
					if (!StringUtils.isBlank(startSub)) {
						try {
							pricePlan.setStartSubscriptionDate(sdf
									.parse(startSub));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid startSub in line "
											+ rowIndex
											+ ", startSub="
											+ startSub
											+ " expected format:"
											+ param.getProperty(
													"excelImport.dateFormat",
													"dd/MM/yyyy")
											+ ", you may change the property excelImport.dateFormat.");
						}
					} else {
						pricePlan.setStartSubscriptionDate(null);
					}

					// endAppli
					if (!StringUtils.isBlank(endSub)) {
						try {
							pricePlan.setEndSubscriptionDate(sdf.parse(endSub));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid endSub in line "
											+ rowIndex
											+ ", endSub="
											+ endSub
											+ " expected format:"
											+ param.getProperty(
													"excelImport.dateFormat",
													"dd/MM/yyyy")
											+ ", you may change the property excelImport.dateFormat.");
						}
					} else {
						pricePlan.setEndSubscriptionDate(null);
					}

					// OfferCode
					if (!StringUtils.isBlank(offerCode)) {
						qb = new QueryBuilder(OfferTemplate.class, "p");
						qb.addCriterion("code", "=", currencyCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<OfferTemplate> offers = qb.getQuery(em)
								.getResultList();
						OfferTemplate offer = null;
						if (offers == null || offers.size() == 0) {
							throw new BusinessException(
									"Invalid offer code in line " + rowIndex
											+ ", code=" + offerCode);
						}
						offer = offers.get(0);
						pricePlan.setOfferTemplate(offer);
					} else {
						pricePlan.setOfferTemplate(null);
					}

					// Priority
					if (!StringUtils.isBlank(priority)) {
						try {
							pricePlan.setPriority(Integer.parseInt(priority));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid priority in line " + rowIndex
											+ ", priority=" + priority);
						}
					} else {
						pricePlan.setPriority(1);
					}

					// AmountWOTax
					if (!StringUtils.isBlank(amountWOTax)) {
						try {
							pricePlan.setAmountWithoutTax(new BigDecimal(
									amountWOTax));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid amount wo tax in line " + rowIndex
											+ ", amountWOTax=" + amountWOTax);
						}
					} else {
						throw new BusinessException("Amount wo tax in line "
								+ rowIndex + " should not be empty");
					}

					// AmountWithTax
					if (!StringUtils.isBlank(amountWithTax)) {
						try {
							pricePlan.setAmountWithTax(new BigDecimal(
									amountWithTax));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid amount wo tax in line " + rowIndex
											+ ", amountWithTax="
											+ amountWithTax);
						}
					} else {
						pricePlan.setAmountWithTax(null);
					}

					// minQuantity
					if (!StringUtils.isBlank(minQuantity)) {
						try {
							pricePlan
									.setMinQuantity(new BigDecimal(minQuantity));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid minQuantity in line " + rowIndex
											+ ", minQuantity=" + minQuantity);
						}
					} else {
						pricePlan.setMinQuantity(null);
					}

					// maxQuantity
					if (!StringUtils.isBlank(maxQuantity)) {
						try {
							pricePlan.setMaxQuantity(new BigDecimal(maxSubAge));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid maxQuantity in line " + rowIndex
											+ ", maxQuantity=" + maxQuantity);
						}
					} else {
						pricePlan.setMaxQuantity(null);
					}

					// Criteria1
					if (!StringUtils.isBlank(criteria1)) {
						try {
							pricePlan.setCriteria1Value(criteria1);
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid criteria1 in line " + rowIndex
											+ ", criteria1=" + criteria1);
						}
					} else {
						pricePlan.setCriteria1Value(null);
					}

					// Criteria2
					if (!StringUtils.isBlank(criteria2)) {
						try {
							pricePlan.setCriteria2Value(criteria2);
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid criteria2 in line " + rowIndex
											+ ", criteria2=" + criteria2);
						}
					} else {
						pricePlan.setCriteria2Value(null);
					}

					// Criteria3
					if (!StringUtils.isBlank(criteria3)) {
						try {
							pricePlan.setCriteria3Value(criteria3);
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid criteria3 in line " + rowIndex
											+ ", criteria3=" + criteria3);
						}
					} else {
						pricePlan.setCriteria3Value(null);
					}

					// CriteriaEL
					if (!StringUtils.isBlank(criteriaEL)) {
						try {
							pricePlan.setCriteriaEL(criteriaEL);
							;
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid criteriaEL in line " + rowIndex
											+ ", criteriaEL=" + criteriaEL);
						}
					} else {
						pricePlan.setCriteriaEL(null);
					}
					if (pricePlan.getId() == null) {
						em.persist(pricePlan);
					}

					// startAppli
					if (!StringUtils.isBlank(startRating)) {
						try {
							pricePlan
									.setStartRatingDate(sdf.parse(startRating));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid startRating in line "
											+ rowIndex
											+ ", startRating="
											+ startRating
											+ " expected format:"
											+ param.getProperty(
													"excelImport.dateFormat",
													"dd/MM/yyyy")
											+ ", you may change the property excelImport.dateFormat.");
						}
					} else {
						pricePlan.setStartRatingDate(null);
					}

					// endAppli
					if (!StringUtils.isBlank(endRating)) {
						try {
							pricePlan.setEndRatingDate(sdf.parse(endRating));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid endRating in line "
											+ rowIndex
											+ ", endRating="
											+ endRating
											+ " expected format:"
											+ param.getProperty(
													"excelImport.dateFormat",
													"dd/MM/yyyy")
											+ ", you may change the property excelImport.dateFormat.");
						}
					} else {
						pricePlan.setEndRatingDate(null);
					}

					// minSubAge
					if (!StringUtils.isBlank(minSubAge)) {
						try {
							pricePlan.setMinSubscriptionAgeInMonth(Long
									.parseLong(minSubAge));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid minSubAge in line " + rowIndex
											+ ", minSubAge=" + minSubAge);
						}
					} else {
						pricePlan.setMinSubscriptionAgeInMonth(0L);
					}

					// maxSubAge
					if (!StringUtils.isBlank(maxSubAge)) {
						try {
							pricePlan.setMaxSubscriptionAgeInMonth(Long
									.parseLong(maxSubAge));
						} catch (Exception e) {
							throw new BusinessException(
									"Invalid maxSubAge in line " + rowIndex
											+ ", maxSubAge=" + maxSubAge);
						}
					} else {
						pricePlan.setMaxSubscriptionAgeInMonth(9999L);
					}
					result++;
				}
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new BusinessException("Error while accessing the excel file.");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new BusinessException("Error while parsing the excel file.");
		}
		return result;
	}

}
