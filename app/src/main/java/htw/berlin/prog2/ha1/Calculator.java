package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten eines Online-Taschenrechners imitiert,
 * der auf https://www.online-calculator.com/ aufgerufen werden kann
 * (ohne die Memory-Funktionen) und dessen Bildschirm bis zu zehn Ziffern
 * plus einem Dezimaltrennzeichen darstellen kann. Enthält mit Absicht
 * diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {

    private String screen = "0";
    private double latestValue;
    private String latestOperation = "";

    /**
     * Gibt den aktuellen Bildschirminhalt als String zurück.
     *
     * @return Der aktuelle Bildschirminhalt.
     */
    public String readScreen() {
        return screen;
    }

    /**
     * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste
     * auf einmal drücken kann, muss der Wert positiv und einstellig sein und
     * zwischen 0 und 9 liegen. Führt dazu, dass die gerade gedrückte Ziffer
     * auf dem Bildschirm angezeigt oder rechts an die zuvor gedrückte Ziffer
     * angehängt wird.
     *
     * @param digit Die Ziffer, deren Taste gedrückt wurde.
     * @throws IllegalArgumentException Wenn die Ziffer nicht zwischen 0 und 9 liegt.
     */
    public void pressDigitKey(int digit) {
        if (digit > 9 || digit < 0) throw new IllegalArgumentException();

        if (screen.equals("0") || latestValue == Double.parseDouble(screen)) screen = "";

        screen = screen + digit;
    }

    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken löscht die zuvor eingegebenen Ziffern auf dem Bildschirm,
     * sodass "0" angezeigt wird, ohne zuvor zwischengespeicherte Werte zu löschen.
     * Bei erneutem Drücken werden auch zwischengespeicherte Werte sowie der aktuelle
     * Operationsmodus zurückgesetzt, sodass der Rechner wieder im Ursprungszustand ist.
     */
    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
    }

    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der
     * vier Operationen: Addition, Substraktion, Division oder Multiplikation,
     * welche zwei Operanden benötigen. Beim ersten Drücken der Taste wird der
     * Bildschirminhalt nicht verändert, sondern nur der Rechner in den passenden
     * Operationsmodus versetzt. Beim zweiten Drücken nach Eingabe einer weiteren
     * Zahl wird direkt das aktuelle Zwischenergebnis auf dem Bildschirm angezeigt.
     * Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     *
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation,
     *                  "/" für Division.
     */
    public void pressBinaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
    }

    /**
     * Empfängt den Wert einer gedrückten unären Operationstaste, also eine der
     * drei Operationen: Quadratwurzel, Prozent, Inversion, welche nur einen
     * Operanden benötigen. Beim Drücken der Taste wird direkt die Operation auf den
     * aktuellen Zahlenwert angewendet und der Bildschirminhalt mit dem Ergebnis
     * aktualisiert.
     *
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion.
     * @throws IllegalArgumentException Wenn die Operation nicht bekannt ist.
     */
    public void pressUnaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;

        switch (operation) {
            case "√":
                screen = Double.toString(Math.sqrt(latestValue));
                break;
            case "%":
                screen = Double.toString(latestValue / 100);
                break;
            case "1/x":
                // Überprüfen, ob der Wert 0 ist
                if (latestValue == 0) {
                    screen = "Error"; // Setze den Bildschirm auf "Error"
                    return; // Beende die Ausführung der Methode
                }
                screen = Double.toString(1 / latestValue); // 1/x Berechnung
                break;
            default:
                throw new IllegalArgumentException();
        }
        screen = Double.toString(latestValue);
        // Fehlerbehandlung für NaN
        if (screen.equals("NaN")) screen = "Error";
        // Begrenze die Anzahl der Stellen nach dem Komma
        if (screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    }


    /**
     * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im
     * Englischen üblicherweise ".". Fügt beim ersten Mal Drücken dem aktuellen
     * Bildschirminhalt das Trennzeichen auf der rechten Seite hinzu und
     * aktualisiert den Bildschirm. Daraufhin eingegebene Zahlen werden rechts
     * vom Trennzeichen angegeben und daher als Dezimalziffern interpretiert.
     * Beim zweiten Drücken, oder wenn bereits ein Trennzeichen angezeigt wird,
     * passiert nichts.
     */
    public void pressDotKey() {
        if (!screen.contains(".")) screen = screen + ".";
    }

    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links
     * angehängt, der Bildschirm aktualisiert und der Inhalt fortan als negativ
     * interpretiert. Zeigt der Bildschirm bereits einen negativen Wert mit
     * führendem Minus an, dann wird dieses entfernt und der Inhalt fortan
     * als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    /**
     * Empfängt den Befehl der gedrückten "="-Taste. Wurde zuvor keine
     * Operationstaste gedrückt, passiert nichts. Wurde zuvor eine binäre
     * Operationstaste gedrückt und zwei Operanden eingegeben, wird das Ergebnis
     * der Operation angezeigt. Falls hierbei eine Division durch Null auftritt,
     * wird "Error" angezeigt. Wird die Taste weitere Male gedrückt (ohne andere
     * Tasten dazwischen), so wird die letzte Operation (ggf. inklusive letztem
     * Operand) erneut auf den aktuellen Bildschirminhalt angewandt und das Ergebnis
     * direkt angezeigt.
     */
    public void pressEqualsKey() {
        var result = switch(latestOperation) {
            case "+" -> latestValue + Double.parseDouble(screen);
            case "-" -> latestValue - Double.parseDouble(screen);
            case "*" -> latestValue * Double.parseDouble(screen);
            case "/" -> latestValue / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };

        if (result == 0) {
            screen = "0";
        } else {
            screen = Double.toString(result);
        }


        // Überprüfen, ob das Ergebnis "Infinity" ist
        if(screen.equals("Infinity")) screen = "Error";
        if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    }
}

