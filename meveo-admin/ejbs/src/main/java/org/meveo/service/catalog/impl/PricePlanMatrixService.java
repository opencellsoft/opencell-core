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
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.collections.IteratorUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
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

@Stateless
public class PricePlanMatrixService extends PersistenceService<PricePlanMatrix> {

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

    public void create(PricePlanMatrix pricePlan, User creator, Provider provider) {
        super.create(pricePlan, creator, provider);
        ratingCacheContainerProvider.addPricePlanToCache(pricePlan);
    }

    @Override
    public PricePlanMatrix disable(PricePlanMatrix pricePlan) {
        pricePlan = super.disable(pricePlan);
        ratingCacheContainerProvider.removePricePlanFromCache(pricePlan);
        return pricePlan;
    }

    @Override
    public PricePlanMatrix enable(PricePlanMatrix pricePlan) {
        pricePlan = super.enable(pricePlan);
        ratingCacheContainerProvider.addPricePlanToCache(pricePlan);
        return pricePlan;
    }

    @Override
    public void remove(PricePlanMatrix pricePlan) {
        super.remove(pricePlan);
        ratingCacheContainerProvider.removePricePlanFromCache(pricePlan);
    }

    @Override
    public PricePlanMatrix update(PricePlanMatrix pricePlan, User updater) {
        pricePlan = super.update(pricePlan, updater);
        ratingCacheContainerProvider.updatePricePlanInCache(pricePlan);
        return pricePlan;
    }
    
    @SuppressWarnings("unchecked")
    public void removeByPrefix(EntityManager em, String prefix, Provider provider) {
        Query query = em.createQuery("select m from PricePlanMatrix m WHERE m.eventCode LIKE '" + prefix + "%' AND m.provider=:provider");
        query.setParameter("provider", provider);
        List<PricePlanMatrix> pricePlans = query.getResultList();
        for (PricePlanMatrix pricePlan : pricePlans) {
            remove(pricePlan);
        }
    }

    @SuppressWarnings("unchecked")
    public void removeByCode(EntityManager em, String code, Provider provider) {
        Query query = em.createQuery("select m PricePlanMatrix m WHERE m.eventCode=:code AND m.provider=:provider");
        query.setParameter("code", code);
        query.setParameter("provider", provider);
        List<PricePlanMatrix> pricePlans = query.getResultList();
        for (PricePlanMatrix pricePlan : pricePlans) {
            remove(pricePlan);
        }
    }

	String[] colNames = { "Charge code", "Seller", "Country", "Currency", "Start appli.", "End appli.", "Offer code",
			"Priority", "Amount w/out tax", "Amount with tax", "Min quantity", "Max quantity", "Criteria 1",
			"Criteria 2", "Criteria 3", "Criteria EL", "Start rating", "End rating", "Min subscr age", "Max subscr age" };

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int importFromExcel(EntityManager em, InputStream excelInputStream, User user, Provider provider)
			throws BusinessException {
		int result = 0;
		Workbook workbook;
		ParamBean param = ParamBean.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(param.getProperty("excelImport.dateFormat", "dd/MM/yyyy"));

		// TODO cache entities
		try {
			workbook = WorkbookFactory.create(excelInputStream);
			Sheet sheet = workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.rowIterator();
			Object[] rowsObj = IteratorUtils.toArray(rowIterator);
			Row row0 = (Row) rowsObj[0];
			Object[] headerCellsObj = IteratorUtils.toArray(row0.cellIterator());

			if (headerCellsObj.length != colNames.length) {
				throw new BusinessException("Invalid number of columns in the excel file.");
			}

			for (int i = 0; i < headerCellsObj.length; i++) {
				if (!colNames[i].equalsIgnoreCase(((Cell) headerCellsObj[i]).getStringCellValue())) {
					throw new BusinessException("Invalid column " + i + " found ["
							+ ((Cell) headerCellsObj[i]).getStringCellValue() + "] but was expecting [" + colNames[i]
							+ "]");
				}
			}

			for (int rowIndex = 1; rowIndex < rowsObj.length; rowIndex++) {
				Row row = (Row) rowsObj[rowIndex];
				Object[] cellsObj = IteratorUtils.toArray(row.cellIterator());

				PricePlanMatrix pricePlan = null;
				QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "p");
				qb.addCriterion("eventCode", "=", ((Cell) cellsObj[0]).getStringCellValue(), false);
				qb.addCriterionEntity("provider", provider);
				@SuppressWarnings("unchecked")
				List<PricePlanMatrix> pricePlans = qb.getQuery(em).getResultList();

				if (pricePlans == null || pricePlans.size() == 0) {
					pricePlan = new PricePlanMatrix();
					pricePlan.setProvider(provider);
					pricePlan.setAuditable(new Auditable());
					pricePlan.getAuditable().setCreated(new Date());
					pricePlan.getAuditable().setCreator(user);
					pricePlan.setEventCode(((Cell) cellsObj[0]).getStringCellValue());
				} else if (pricePlans.size() == 1) {
					pricePlan = pricePlans.get(0);
				}

				if (pricePlan == null) {
					log.warn("There are several pricePlan records for charge "
							+ ((Cell) cellsObj[0]).getStringCellValue() + " we do not update them.");
				} else {
					int i = 1;
					String sellerCode = ((Cell) cellsObj[i++]).getStringCellValue();
					String countryCode = ((Cell) cellsObj[i++]).getStringCellValue();
					String currencyCode = ((Cell) cellsObj[i++]).getStringCellValue();
					String startSub = ((Cell) cellsObj[i++]).getStringCellValue();
					String endSub = ((Cell) cellsObj[i++]).getStringCellValue();
					String offerCode = ((Cell) cellsObj[i++]).getStringCellValue();
					String priority = ((Cell) cellsObj[i++]).getStringCellValue() + "";
					String amountWOTax = ((Cell) cellsObj[i++]).getStringCellValue() + "";
					String amountWithTax = ((Cell) cellsObj[i++]).getStringCellValue() + "";
					String minQuantity = ((Cell) cellsObj[i++]).getStringCellValue() + "";
					String maxQuantity = ((Cell) cellsObj[i++]).getStringCellValue() + "";
					String criteria1 = ((Cell) cellsObj[i++]).getStringCellValue();
					String criteria2 = ((Cell) cellsObj[i++]).getStringCellValue();
					String criteria3 = ((Cell) cellsObj[i++]).getStringCellValue();
					String criteriaEL = ((Cell) cellsObj[i++]).getStringCellValue();
					String startRating = ((Cell) cellsObj[i++]).getStringCellValue();
					String endRating = ((Cell) cellsObj[i++]).getStringCellValue();
					String minSubAge = ((Cell) cellsObj[i++]).getStringCellValue() + "";
					String maxSubAge = ((Cell) cellsObj[i++]).getStringCellValue() + "";

					log.debug(
							"sellerCode={}, countryCode={}, currencyCode={}, startSub={}, endSub={}, offerCode={}, priority={}, amountWOTax={}, amountWithTax={}, minQuantity={}, maxQuantity={}, criteria1={}, criteria2={}, criteria3={}, criteriaEL={}, startRating={}, endRating={}, minSubAge={}, maxSubAge={}",
							new Object[] { sellerCode, countryCode, currencyCode, startSub, endSub, offerCode,
									priority, amountWOTax, amountWithTax, minQuantity, maxQuantity, criteria1,
									criteria2, criteria3, criteriaEL, startRating, endRating, minSubAge, maxSubAge });

					// Seller
					if (!StringUtils.isBlank(sellerCode)) {
						qb = new QueryBuilder(Seller.class, "p");
						qb.addCriterion("code", "=", sellerCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<Seller> sellers = qb.getQuery(em).getResultList();
						Seller seller = null;

						if (sellers == null || sellers.size() == 0) {
							throw new BusinessException("Invalid seller in line=" + rowIndex + ", code=" + sellerCode);
						}

						seller = sellers.get(0);
						pricePlan.setSeller(seller);
					} else {
						pricePlan.setSeller(null);
					}

					// Country
					if (!StringUtils.isBlank(countryCode)) {
						qb = new QueryBuilder(TradingCountry.class, "p");
						qb.addCriterion("p.country.countryCode", "=", countryCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<TradingCountry> countries = qb.getQuery(em).getResultList();
						TradingCountry tradingCountry = null;

						if (countries == null || countries.size() == 0) {
							throw new BusinessException("Invalid country in line=" + rowIndex + ", code=" + countryCode);
						}

						tradingCountry = countries.get(0);
						pricePlan.setTradingCountry(tradingCountry);
					} else {
						pricePlan.setTradingCountry(null);
					}

					// Currency
					if (!StringUtils.isBlank(currencyCode)) {
						qb = new QueryBuilder(TradingCurrency.class, "p");
						qb.addCriterion("p.currency.currencyCode", "=", currencyCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<TradingCurrency> currencies = qb.getQuery(em).getResultList();
						TradingCurrency tradingCurrency = null;

						if (currencies == null || currencies.size() == 0) {
							throw new BusinessException("Invalid currency in line=" + rowIndex + ", code="
									+ countryCode);
						}

						tradingCurrency = currencies.get(0);
						pricePlan.setTradingCurrency(tradingCurrency);
					} else {
						pricePlan.setTradingCurrency(null);
					}

					// startAppli
					if (!StringUtils.isBlank(startSub)) {
						try {
							pricePlan.setStartSubscriptionDate(sdf.parse(startSub));
						} catch (Exception e) {
							throw new BusinessException("Invalid startSub in line=" + rowIndex + ", startSub="
									+ startSub + " expected format:"
									+ param.getProperty("excelImport.dateFormat", "dd/MM/yyyy")
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
							throw new BusinessException("Invalid endSub in line=" + rowIndex + ", endSub=" + endSub
									+ " expected format:" + param.getProperty("excelImport.dateFormat", "dd/MM/yyyy")
									+ ", you may change the property excelImport.dateFormat.");
						}
					} else {
						pricePlan.setEndSubscriptionDate(null);
					}

					// OfferCode
					if (!StringUtils.isBlank(offerCode)) {
						qb = new QueryBuilder(OfferTemplate.class, "p");
						qb.addCriterion("code", "=", offerCode, false);
						qb.addCriterionEntity("provider", provider);
						@SuppressWarnings("unchecked")
						List<OfferTemplate> offers = qb.getQuery(em).getResultList();
						OfferTemplate offer = null;

						if (offers == null || offers.size() == 0) {
							throw new BusinessException("Invalid offer code in line=" + rowIndex + ", code="
									+ offerCode);
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
							throw new BusinessException("Invalid priority in line=" + rowIndex + ", priority="
									+ priority);
						}
					} else {
						pricePlan.setPriority(1);
					}

					// AmountWOTax
					if (!StringUtils.isBlank(amountWOTax)) {
						try {
							pricePlan.setAmountWithoutTax(new BigDecimal(amountWOTax));
						} catch (Exception e) {
							throw new BusinessException("Invalid amount wo tax in line=" + rowIndex + ", amountWOTax="
									+ amountWOTax);
						}
					} else {
						throw new BusinessException("Amount wo tax in line=" + rowIndex + " should not be empty");
					}

					// AmountWithTax
					if (!StringUtils.isBlank(amountWithTax)) {
						try {
							pricePlan.setAmountWithTax(new BigDecimal(amountWithTax));
						} catch (Exception e) {
							throw new BusinessException("Invalid amount wo tax in line=" + rowIndex
									+ ", amountWithTax=" + amountWithTax);
						}
					} else {
						pricePlan.setAmountWithTax(null);
					}

					// minQuantity
					if (!StringUtils.isBlank(minQuantity)) {
						try {
							pricePlan.setMinQuantity(new BigDecimal(minQuantity));
						} catch (Exception e) {
							throw new BusinessException("Invalid minQuantity in line=" + rowIndex + ", minQuantity="
									+ minQuantity);
						}
					} else {
						pricePlan.setMinQuantity(null);
					}

					// maxQuantity
					if (!StringUtils.isBlank(maxQuantity)) {
						try {
							pricePlan.setMaxQuantity(new BigDecimal(maxSubAge));
						} catch (Exception e) {
							throw new BusinessException("Invalid maxQuantity in line=" + rowIndex + ", maxQuantity="
									+ maxQuantity);
						}
					} else {
						pricePlan.setMaxQuantity(null);
					}

					// Criteria1
					if (!StringUtils.isBlank(criteria1)) {
						try {
							pricePlan.setCriteria1Value(criteria1);
						} catch (Exception e) {
							throw new BusinessException("Invalid criteria1 in line=" + rowIndex + ", criteria1="
									+ criteria1);
						}
					} else {
						pricePlan.setCriteria1Value(null);
					}

					// Criteria2
					if (!StringUtils.isBlank(criteria2)) {
						try {
							pricePlan.setCriteria2Value(criteria2);
						} catch (Exception e) {
							throw new BusinessException("Invalid criteria2 in line=" + rowIndex + ", criteria2="
									+ criteria2);
						}
					} else {
						pricePlan.setCriteria2Value(null);
					}

					// Criteria3
					if (!StringUtils.isBlank(criteria3)) {
						try {
							pricePlan.setCriteria3Value(criteria3);
						} catch (Exception e) {
							throw new BusinessException("Invalid criteria3 in line=" + rowIndex + ", criteria3="
									+ criteria3);
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
							throw new BusinessException("Invalid criteriaEL in line=" + rowIndex + ", criteriaEL="
									+ criteriaEL);
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
							pricePlan.setStartRatingDate(sdf.parse(startRating));
						} catch (Exception e) {
							throw new BusinessException("Invalid startRating in line=" + rowIndex + ", startRating="
									+ startRating + " expected format:"
									+ param.getProperty("excelImport.dateFormat", "dd/MM/yyyy")
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
							throw new BusinessException("Invalid endRating in line=" + rowIndex + ", endRating="
									+ endRating + " expected format:"
									+ param.getProperty("excelImport.dateFormat", "dd/MM/yyyy")
									+ ", you may change the property excelImport.dateFormat.");
						}
					} else {
						pricePlan.setEndRatingDate(null);
					}

					// minSubAge
					if (!StringUtils.isBlank(minSubAge)) {
						try {
							pricePlan.setMinSubscriptionAgeInMonth(Long.parseLong(minSubAge));
						} catch (Exception e) {
							throw new BusinessException("Invalid minSubAge in line=" + rowIndex + ", minSubAge="
									+ minSubAge);
						}
					} else {
						pricePlan.setMinSubscriptionAgeInMonth(0L);
					}

					// maxSubAge
					if (!StringUtils.isBlank(maxSubAge)) {
						try {
							pricePlan.setMaxSubscriptionAgeInMonth(Long.parseLong(maxSubAge));
						} catch (Exception e) {
							throw new BusinessException("Invalid maxSubAge in line=" + rowIndex + ", maxSubAge="
									+ maxSubAge);
						}
					} else {
						pricePlan.setMaxSubscriptionAgeInMonth(9999L);
					}
					result++;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new BusinessException("Error while accessing the excel file.");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new BusinessException("Error while parsing the excel file.");
		}

		return result;
	}

	public PricePlanMatrix findByCode(String code, Provider provider) {
		QueryBuilder qb = new QueryBuilder(PricePlanMatrix.class, "m", null, provider);
		qb.addCriterion("code", "=", code, true);

		try {
			return (PricePlanMatrix) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

    /**
     * Get a list of priceplans to populate a cache
     * 
     * @return A list of active priceplans
     */
    public List<PricePlanMatrix> getPricePlansForCache() {
        return getEntityManager().createNamedQuery("PricePlanMatrix.getPricePlansForCache", PricePlanMatrix.class).getResultList();
    }
}
