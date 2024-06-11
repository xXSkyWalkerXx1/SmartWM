package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import org.springframework.lang.NonNull;

public enum SecuritiesType {
    SHARE("Aktien"),
    BOND("Anleihen"),
    CERTIFICATE("Zertifikate"),
    ETF("ETF"),
    ETC("ETC"),
    FOND("Fonds"),
    RESOURCE("Rohstoffe");


    final String displayText;

    SecuritiesType(@NonNull String displayText){
        this.displayText = displayText;
    }

    /**
     * Maps the type, given by the excel-sheet, to an instance of {@link SecuritiesType}.
     * @param type A = Aktie, BZR = Bezugsrechte; FA = Aktienfond; FAD = deutsche Aktienfonds, FAA = ausländischer FA;
     *             FAE = europäische FA, FV = Gemischte Fond; FVE = europäische FV; FVA = internationale FV;
     *             FG = Geldmarktfond; FGD = deutsche FG, FGE = europäische FG, FGA = ausländische FG,
     *             FIM = Immobilienfond; FR = Rentenfond / O = Obligationen; FRD = deutesche FR, FRA = Internat. FR,
     *             HF = Hedg, S = Spezialitäten, R = festferzinsliche Wertpapiere; RD = deutsche R, RA = ausländische R,
     *             Rst = Rohstoffe, Z = Zertifikate
     * @// TODO: 22.05.2024 Implement mapping of the other types!
     */
    @NonNull
    public static SecuritiesType getMapped(@NonNull String type) throws IllegalArgumentException{
        switch (type){
            case "Z" -> {
                return SecuritiesType.CERTIFICATE;
            }
            case "Rst" -> {
                return SecuritiesType.RESOURCE;
            }
            case "A" -> {
                return SecuritiesType.SHARE;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public String getDisplayText() {
        return displayText;
    }
}
