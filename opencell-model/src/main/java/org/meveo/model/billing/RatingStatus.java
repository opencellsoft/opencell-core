package org.meveo.model.billing;

public class RatingStatus {

    int nbRating = 0;
    RatingStatusEnum status;

    public int getNbRating() {
        return nbRating;
    }

    public void setNbRating(int nbRating) {
        this.nbRating = nbRating;
    }

    public RatingStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RatingStatusEnum status) {
        this.status = status;
    }

}
