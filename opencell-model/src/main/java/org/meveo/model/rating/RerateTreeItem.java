package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.meveo.model.billing.Amounts;


public class RerateTreeItem {
	private String edrIds;
	private String woIds;
	private String rtIds;
	private Long ilId;
	private rtSummary rtAmounts;

	private String dwoIds;
	private String drtIds;
	private Long dilId;
	private rtSummary drtAmounts;

	public List<Long> getEdrIdsList() {
		return edrIdsList;
	}

	public void setEdrIdsList(List<Long> edrIdsList) {
		this.edrIdsList = edrIdsList;
	}

	public List<Long> getWoIdsList() {
		return woIdsList;
	}

	public void setWoIdsList(List<Long> woIdsList) {
		this.woIdsList = woIdsList;
	}

	public List<Long> getRtIdsList() {
		return rtIdsList;
	}

	public void setRtIdsList(List<Long> rtIdsList) {
		this.rtIdsList = rtIdsList;
	}

	public List<Long> getDwoIdsList() {
		return dwoIdsList;
	}

	public void setDwoIdsList(List<Long> dwoIdsList) {
		this.dwoIdsList = dwoIdsList;
	}

	public List<Long> getDrtIdsList() {
		return drtIdsList;
	}

	public void setDrtIdsList(List<Long> drtIdsList) {
		this.drtIdsList = drtIdsList;
	}

	public List<Long> getTwoIdsList() {
		return twoIdsList;
	}

	public void setTwoIdsList(List<Long> twoIdsList) {
		this.twoIdsList = twoIdsList;
	}

	public List<Long> getTrtIdsList() {
		return trtIdsList;
	}

	public void setTrtIdsList(List<Long> trtIdsList) {
		this.trtIdsList = trtIdsList;
	}

	public void setEdrIds(String edrIds) {
		this.edrIds = edrIds;
	}

	public void setWoIds(String woIds) {
		this.woIds = woIds;
	}

	public void setRtIds(String rtIds) {
		this.rtIds = rtIds;
	}

	public void setDwoIds(String dwoIds) {
		this.dwoIds = dwoIds;
	}

	public void setDrtIds(String drtIds) {
		this.drtIds = drtIds;
	}

	public void setTwoIds(String twoIds) {
		this.twoIds = twoIds;
	}

	public void setTrtIds(String trtIds) {
		this.trtIds = trtIds;
	}

	private String twoIds;
	private String trtIds;
	private Long tilId;
	private rtSummary trtAmounts;
	
	private List<Long> edrIdsList;
	private List<Long> woIdsList;
	private List<Long> rtIdsList;
	private List<Long> dwoIdsList;
	private List<Long> drtIdsList;
	private List<Long> twoIdsList;
	private List<Long> trtIdsList;

	private Long subscriptionId;
	private Long billedIl;
	private Long countWo;

	public RerateTreeItem(String edrIds, String woIds, String rtIds, Long ilId, BigDecimal rtAmountWithoutTax,
			BigDecimal rtAmountWithTax, BigDecimal rtAmountTax, BigDecimal rtQuantity, String dwoIds, String drtIds,
			Long dilId, BigDecimal drtAmountWithoutTax, BigDecimal drtAmountWithTax, BigDecimal drtAmountTax,
			BigDecimal drtQuantity, String twoIds, String trtIds, Long tilId, BigDecimal trtAmountWithoutTax,
			BigDecimal trtAmountWithTax, BigDecimal trtAmountTax, BigDecimal trtQuantity, long subscriptionId,
			Long billedIl, Long countWo) {
		this.edrIds=edrIds;
		this.edrIdsList = toLongs(edrIds);

		this.woIds = woIds;
		this.dwoIds = dwoIds;
		this.twoIds = twoIds;
		this.woIdsList = toLongs(woIds);
		this.dwoIdsList = toLongs(dwoIds);
		this.twoIdsList = toLongs(twoIds);

		this.rtIds = rtIds;
		this.trtIds = trtIds;
		this.drtIds = drtIds;
		this.rtIdsList = toLongs(rtIds);
		this.trtIdsList = toLongs(trtIds);
		this.drtIdsList = toLongs(drtIds);

		this.rtAmounts = new rtSummary(rtAmountWithoutTax, rtAmountWithTax, rtAmountTax, rtQuantity);
		this.trtAmounts = new rtSummary(rtAmountWithoutTax, trtAmountWithTax, trtAmountTax, trtQuantity);
		this.drtAmounts = new rtSummary(drtAmountWithoutTax, drtAmountWithTax, drtAmountTax, drtQuantity);

		this.ilId = ilId;
		this.dilId = dilId;
		this.tilId = tilId;

		this.billedIl = billedIl;
		this.countWo = countWo;
		this.subscriptionId = subscriptionId;

	}

	private List<Long> toLongs(String edrIds) {
		return edrIds == null ? null : Arrays.stream(edrIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
	}

	public List<Long> getEdrIds() {
		return edrIdsList;
	}

	public void setEdrIds(List<Long> edrIds) {
		this.edrIdsList = edrIds;
	}

	public List<Long> getWoIds() {
		return woIdsList;
	}

	public void setWoIds(List<Long> woIds) {
		this.woIdsList = woIds;
	}

	public List<Long> getRtIds() {
		return rtIdsList;
	}

	public void setRtIds(List<Long> rtIds) {
		this.rtIdsList = rtIds;
	}

	public Long getIlId() {
		return ilId;
	}

	public void setIlId(Long ilId) {
		this.ilId = ilId;
	}

	public rtSummary getRtAmounts() {
		return rtAmounts;
	}

	public void setRtAmounts(rtSummary rtAmounts) {
		this.rtAmounts = rtAmounts;
	}

	public List<Long> getDwoIds() {
		return dwoIdsList;
	}

	public void setDwoIds(List<Long> dwoIds) {
		this.dwoIdsList = dwoIds;
	}

	public List<Long> getDrtIds() {
		return drtIdsList;
	}

	public void setDrtIds(List<Long> drtIds) {
		this.drtIdsList = drtIds;
	}

	public Long getDilId() {
		return dilId;
	}

	public void setDilId(Long dilId) {
		this.dilId = dilId;
	}

	public rtSummary getDrtAmounts() {
		return drtAmounts;
	}

	public void setDrtAmounts(rtSummary drtAmounts) {
		this.drtAmounts = drtAmounts;
	}

	public List<Long> getTwoIds() {
		return twoIdsList;
	}

	public void setTwoIds(List<Long> twoIds) {
		this.twoIdsList = twoIds;
	}

	public List<Long> getTrtIds() {
		return trtIdsList;
	}

	public void setTrtIds(List<Long> trtIds) {
		this.trtIdsList = trtIds;
	}

	public Long getTilId() {
		return tilId;
	}

	public void setTilId(Long tilId) {
		this.tilId = tilId;
	}

	public rtSummary getTrtAmounts() {
		return trtAmounts;
	}

	public void setTrtAmounts(rtSummary trtAmounts) {
		this.trtAmounts = trtAmounts;
	}

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Long getBilledIl() {
		return billedIl;
	}

	public void setBilledIl(Long billedIl) {
		this.billedIl = billedIl;
	}

	public Long getCountWo() {
		return countWo;
	}

	public void setCountWo(Long countWo) {
		this.countWo = countWo;
	}

	public static class rtSummary extends Amounts {
		private BigDecimal quantity;

		public rtSummary(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax,
				BigDecimal quantity) {
			super(amountWithoutTax != null ? amountWithoutTax.negate() : null,
					amountWithTax != null ? amountWithTax.negate() : null,
					amountTax != null ? amountTax.negate() : null);
			this.quantity = quantity.negate();
			this.quantity = quantity;
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public void setQuantity(BigDecimal quantity) {
			this.quantity = quantity;
		}

	}

}