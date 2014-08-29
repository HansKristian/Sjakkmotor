package no.mesan.sjakk.hk_motor;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Brikke;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

import java.util.ArrayList;
import java.util.List;

public class MotorHansKristian extends AbstraktSjakkmotor {
    private float mattSannsynighet = 0;
    private int materiellDiff = 0;
    private boolean funnetMatt = false;

    @Override
    public void finnBesteTrekk(final Posisjon posisjon) {
        List<Trekk> alleLovligeTrekk = posisjon.alleTrekk();

        //Sett ett tilfeldig trekk
        settTilfeldigTrekk(alleLovligeTrekk);

        mattSannsynighet = 0;
        materiellDiff = 0;
        funnetMatt = false;

        int forsteListe, andreListe;

        System.out.println("Ser etter matt...");
        //Se om du kan finne et matt trekk neste runde
        if (finnMattDetteTrekk(alleLovligeTrekk, true)) {
            System.out.println("Sjakkmatt!");
            return;
        }

        forsteListe = alleLovligeTrekk.size();
        System.out.print("Sorterer vekk motstanderens matt-trekk...");
        //Sorter ut trekk som gjør at motstanderen kan sette matt, ta dronning eller få dronning.
        List<Trekk> tryggeTrekk = taVekkMattTrekk(alleLovligeTrekk, posisjon);
        andreListe = tryggeTrekk.size();
        System.out.println(forsteListe - andreListe + " (trekk fjernet)");

        System.out.println("Ser etter tvungen matt i to trekk...");
        //Se om du kan gjøre et trekk med > 1% sannsynlighet for oppfølging i matt
        if (finnMattNesteTrekk(tryggeTrekk, posisjon, false) && mattSannsynighet == 1) return;

        System.out.println("Ser etter bondeforvandling...");
        //Se om du kan få ny dronning neste runde
        if (lagDronningDetteTrekk(tryggeTrekk, true)) return;

        forsteListe = tryggeTrekk.size();
        System.out.print("Fjerner trekk der motstanderen kan slå dronningen.");
        //Sorterer ut trekk der motstander kan vinne materiell verdi
        List<Trekk> tryggereTrekk = taVekkTrekkDerMotstanderKanSlåDronning(tryggeTrekk, posisjon);
        andreListe = tryggereTrekk.size();
        System.out.println(forsteListe - andreListe + " (trekk fjernet)");


        System.out.println("Ser på sannsynligheten på matt med to trekk...");
        if (finnMattNesteTrekk(tryggereTrekk, posisjon, true)) {

            System.out.println("Ser etter trekk som øker materiell MER enn motstanderen kan følgende tur.");
            //Gjør et trekk som øker materiell MER enn det mostanderen kan gjøre følgende tur.
            finnTrekkSomOkerMateriell(tryggereTrekk, posisjon, false, true);

            //Om du kan vinne dronning gjør du heller det!
            if (materiellDiff == 900) {
                System.out.println("Kan vinne dronning, prioriterer dette.");
                finnTrekkSomOkerMateriell(tryggereTrekk, posisjon, true, false);
                return;
            }

            System.out.println("Vurderer sannsynlighet for matt opp mot materiell vinning...");
            //Dersom du har mer enn 10% sjanse for matt bør du gå for det med mindre du kan vinne offiser i stedet.
            if (mattSannsynighet >= 0.7 && materiellDiff < 300) {
                posisjon.gjorTrekk(besteTrekk());
                    if (finnTrekkSomOkerMateriell(posisjon.alleTrekk(), posisjon, false, false) && materiellDiff > 100) {
                        System.out.println("70% sjanse for matt, men motstanderen kan vinne materiell etterpå.");
                    } else {
                        System.out.println("mer enn 70% sjanse for matt, og verken jeg eller motstanderen kan vinne en offiser.");
                        return;
                    }
                posisjon.taTilbakeSisteTrekk();
            }

            //Dersom du kan vinne materiell verdi, gjør det.
            if (materiellDiff > 0) {
                System.out.println("Kan ikke vinne materiell, og under 70% sjanse for matt ");
                finnTrekkSomOkerMateriell(tryggereTrekk, posisjon, true, false);
                return;
            }

        }

        System.out.println("Ser etter trekk som øker materiell MER enn motstanderen kan følgende tur.");
        //Gjør et trekk som øker materiell MER enn det mostanderen kan gjøre følgende tur.
        if (finnTrekkSomOkerMateriell(tryggereTrekk, posisjon, true, true)) return;

        forsteListe = tryggereTrekk.size();
        System.out.print("Sorterer vekk trekk der motstanderen kan vinne materiell verdi. (beta) ");
        //Sorter ut trekk som gjør at motstanderen kan sette matt, ta dronning eller få dronning.
        List<Trekk> posisjonelleTrekk = taVekkFarligeTrekk(tryggereTrekk, posisjon);
        andreListe = posisjonelleTrekk.size();
        System.out.println(forsteListe - andreListe + " (trekk fjernet)");

        System.out.println("Velger et tlfeldig trekk.");
        settTilfeldigTrekk(posisjonelleTrekk);


    }

    public boolean finnMattNesteTrekk(List<Trekk> trekkliste, Posisjon posisjon, boolean logg) {
        boolean fantMattDetteTrekket = false;
        float antallGunstige = 0;
        float antallMulige = 0;

        //Min tur, gjør systematisk trekk
        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            posisjon.gjorTrekk(trekkliste.get(i));

            fantMattDetteTrekket = false;
            antallGunstige = 0;
            antallMulige = 0;

            //Motstanderen gjør systematisk trekk
            for (int i2 = 0; !erStoppet() && i2 < posisjon.alleTrekk().size(); i2++) {
                posisjon.gjorTrekk(posisjon.alleTrekk().get(i2));

                //Hvis jeg kan ta matt nå?
                if (finnMattDetteTrekk(posisjon.alleTrekk(), false)) {
                    //Tell mattmuligheter for trekkliste.get(i)
                    antallGunstige += 1;

                    //Sett til foreløpig beste dersom ikke noe bedre allerede
                    if (!funnetMatt) {
                        funnetMatt = true;
                        settBesteTrekk(trekkliste.get(i));
                    }

                    fantMattDetteTrekket = true;
                } else {
                    //we need to go deeper...
                }

                posisjon.taTilbakeSisteTrekk();
            }

            if (fantMattDetteTrekket) {

                antallMulige = posisjon.alleTrekk().size();
                float p = antallGunstige / antallMulige;

                if (logg) System.out.println("Fant trekk med " + p * 100 + "% sannsynligeht for matt mitt neste trekk.");

                if (p > mattSannsynighet) {
                    mattSannsynighet = p;
                    settBesteTrekk(trekkliste.get(i));
                }
            }
            posisjon.taTilbakeSisteTrekk();
        }

        return funnetMatt;
    }

    public boolean finnTrekkSomOkerMateriell(List<Trekk> trekkliste, Posisjon posisjon, boolean settBesteTrekk, boolean logg) {
        int materiell = posisjon.sumAvMateriellPaaBrettet();
        boolean motstanderenKanGjøreBedre = false;
        materiellDiff = 0;

        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            Trekk trekk = trekkliste.get(i);
            //Gjør hypotetisk trekk
            posisjon.gjorTrekk(trekk);

            //Se om trekket vinner materielt
            if (posisjon.sumAvMateriellPaaBrettet() < materiell * -1) {

                int diff = (materiell * -1) - posisjon.sumAvMateriellPaaBrettet();


                //Se om motstanderen vinner MER materiell etterpå.
                for (int i2 = 0; !erStoppet() && i2 < posisjon.alleTrekk().size(); i2++) {
                    int nyMateriell = posisjon.sumAvMateriellPaaBrettet();

                    posisjon.gjorTrekk(posisjon.alleTrekk().get(i2));

                    int diff2 = (nyMateriell * -1) - posisjon.sumAvMateriellPaaBrettet();

                    motstanderenKanGjøreBedre = diff2 > diff;

                    posisjon.taTilbakeSisteTrekk();

                    if (motstanderenKanGjøreBedre) {
                        if (diff > 0 && logg)
                            System.out.println("Ser mulighet for å vinne " + diff + " materiell, men motstanderen kan vinne " + diff2 + " kommende runde.");

                        break;
                    }
                }

                if (!motstanderenKanGjøreBedre) {
                    if (diff >= materiellDiff) {
                        if (settBesteTrekk) {
                            System.out.println("Ser et trekk for å vinne " + diff + " materiell verdi");
                            settBesteTrekk(trekk);
                        }
                        materiellDiff = diff;
                    }
                }

            } //if materiell på mrettet

            //Undo trekket
            posisjon.taTilbakeSisteTrekk();
        }
        return materiellDiff > 0;
    }

    public boolean finnMattDetteTrekk(List<Trekk> trekkliste, boolean settTilBesteTrekk) {
        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            Trekk trekk =  trekkliste.get(i);

            if(trekk.erSjakkMatt()) {
                if (settTilBesteTrekk)
                    settBesteTrekk(trekk);
                return true;
            }
        }
        return false;
    }

    public boolean lagDronningDetteTrekk(List<Trekk> trekkliste, boolean settTilBesteTrekk) {
        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            Trekk trekk =  trekkliste.get(i);

            if(trekk.brikkeEtterBondeforvandling().equals(Brikke.DRONNING)) {
                if (settTilBesteTrekk) {
                    settBesteTrekk(trekk);
                }
                return true;
            }
        }
        return false;
    }

    public boolean kanSlåDronning(List<Trekk> trekkliste) {
        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            if (trekkliste.get(i).erSlag() && (trekkliste.get(i).brikkeSomSlaas() == Brikke.DRONNING)) {
                return true;
            }
        }
        return false;
    }

    public void settTilfeldigTrekk(List<Trekk> trekkliste) {
        int index = (int) ((trekkliste.size() - 1) * Math.random());
        settBesteTrekk(trekkliste.get(index));
    }

    public List<Trekk> taVekkMattTrekk(List<Trekk> trekkliste, Posisjon posisjon) {
        List<Trekk> tryggeTrekk = new ArrayList<Trekk>();

        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            Trekk trekk = trekkliste.get(i);
            posisjon.gjorTrekk(trekk);

            if (finnMattDetteTrekk(posisjon.alleTrekk(), false)) {
                //Vil ikke gå i matt
            } else {
                tryggeTrekk.add(trekk);
            }

            posisjon.taTilbakeSisteTrekk();
        }
        if (tryggeTrekk.isEmpty()) {
            System.out.println("Huff, GGWP.");
            return trekkliste;
        } else {
            return tryggeTrekk;
        }
    }

    public List<Trekk> taVekkTrekkDerMotstanderKanSlåDronning(List<Trekk> trekkliste, Posisjon posisjon) {
        List<Trekk> tryggereTrekk = new ArrayList<Trekk>();

        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            Trekk trekk = trekkliste.get(i);
            posisjon.gjorTrekk(trekk);

            if (kanSlåDronning(posisjon.alleTrekk())) {
                //Vil ikke miste dronningen min
            } else {
                tryggereTrekk.add(trekk);
            }

            posisjon.taTilbakeSisteTrekk();
        }
        if (tryggereTrekk.isEmpty()) {
            System.out.println("Huff, ingen trekk der jeg ikke kan miste dronnigen.");
            return trekkliste;
        } else return tryggereTrekk;
    }

    public List<Trekk> taVekkFarligeTrekk(List<Trekk> trekkliste, Posisjon posisjon) {
        List<Trekk> trekkUtenMateriellTap = new ArrayList();
        Trekk trekkMedMinstMuligMateriellTap = trekkliste.get(0);
        int lavesteDiff = 0;
        materiellDiff = 0;

        for (int i = 0; !erStoppet() && i < trekkliste.size(); i++) {
            Trekk trekk = trekkliste.get(i);
            posisjon.gjorTrekk(trekk);

            //Ser hvilke materiell motstanderen kan vinne, og unngår der.
            if (finnTrekkSomOkerMateriell(posisjon.alleTrekk(), posisjon, false, false)) {
                if (materiellDiff < lavesteDiff) {
                    lavesteDiff = materiellDiff;
                    trekkMedMinstMuligMateriellTap = trekkliste.get(i);
                }
            } else {
                trekkUtenMateriellTap.add(trekk);
            }

            posisjon.taTilbakeSisteTrekk();
        }

        if (trekkUtenMateriellTap.isEmpty()) {
            System.out.println("Ingen trekk uten materiell tap, velger det med lavest tap");
            List<Trekk> trekklisteMedSikresteTrekk = new ArrayList<Trekk>();
            trekklisteMedSikresteTrekk.add(trekkMedMinstMuligMateriellTap);
            return trekklisteMedSikresteTrekk;
        } else {
            return trekkUtenMateriellTap;
        }
    }

    @Override
    public String lagetAv() {
        return "Hans Kristian Rykkelid";
    }

    @Override
    public String navn() {
        return "Matt-teus";
    }

    public static void main(final String[] args) {
        new MotorHansKristian().start();
    }
}
