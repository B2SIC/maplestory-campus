package maplestory.service.authentic.domain;

import lombok.Data;

@Data
public class Authentic {
    private Integer cerniumLevel;
    private Integer cerniumCount;
    private Integer dailyAmount;  // 세르니움 일일 심볼 획득 개수

    private Integer arcsLevel;
    private Integer arcsCount;

    private Integer odiumLevel;
    private Integer odiumCount;

    private Integer goalAuthentic;

    public Authentic(){
        this.cerniumCount = 0;
        this.arcsCount = 0;
        this.odiumCount = 0;
    }
}
