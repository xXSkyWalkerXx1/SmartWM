package de.tud.inf.mmt.wmscrape.gui.tabs.historic.data;

import org.springframework.lang.NonNull;

public enum SecuritiesType {
    SHARE("Aktien"),
    BOND("Anleihen"),
    CERTIFICATE("Zertifikate"),
    ETF("ETF"),
    FOND("Fonds"),
    RESOURCE("Rohstoffe");


    final String displayText;

    SecuritiesType(@NonNull String displayText){
        this.displayText = displayText;
    }

    /***
     * Maps the type, given by the excel-sheet, to an instance of {@link SecuritiesType}.
     * @param type A = Aktie, BZR = Bezugsrechte; FA = Aktienfond; FAD = deutsche Aktienfonds, FAA = ausländischer FA;
     *             FAE = europäische FA, FV = Gemischte Fond; FVE = europäische FV; FVA = internationale FV;
     *             FG = Geldmarktfond; FGD = deutsche FG, FGE = europäische FG, FGA = ausländische FG,
     *             FIM = Immobilienfond; FR = Rentenfond / O = Obligationen; FRD = deutesche FR, FRA = Internat. FR,
     *             HF = Hedg, S = Spezialitäten, R = festferzinsliche Wertpapiere; RD = deutsche R, RA = ausländische R,
     *             Rst = Rohstoffe, Z = Zertifikate
     * @// TODO: 22.05.2024 Implement mapping of the other types!
     */
    public static SecuritiesType getMapped(@NonNull String type){
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
            default -> {
                String msg = String.format("Der Wertpapier-Typ %s kann nicht gemappt werden.", type);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    public String getDisplayText() {
        return displayText;
    }
}
