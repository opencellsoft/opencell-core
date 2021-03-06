package org.meveo.apiv2.accounts;

public enum OpenTransactionsActionEnum {
    NONE, // no action
    FAIL, // will fail if there’s an OPEN wallet operation or OPEN rated transactions from the subscription on the origin user account
    MOVE, // will move all OPEN wallet operations and OPEN rated transactions produced by the subscription from the origin user account to the target user account
    MOVE_AND_RERATE,// will move and pass to TO_RERATE all “Not Billed” wallet operations (*) produced by the subscription from the origin user account to the target user account
}
