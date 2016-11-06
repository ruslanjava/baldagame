package com.github.ruslanjava.baldagame.dictionaryGenerator.textDictionaryReader;

import java.util.HashMap;
import java.util.Map;

class PluralReplacer {

    private static final Map<String, String> EXCEPTIONS = new HashMap<String, String>() {{
        put("черкесы", "черкес");
        put("чертог", "чертог");
        put("штиблеты", "штиблета");
        put("столбцы", "стоблец");
    }};

    private static final Map<String, String> PLURAL_TO_SINGULAR = new HashMap<String, String>() {{
        put("-а", "и,ы");
        put("-аец", "айцы");
        put("-ак", "аки");
        put("-ал", "алы");
        put("-алец", "альцы");
        put("-анец", "анцы");
        put("-анин", "ане");
        put("-ар", "ары");
        put("-ас", "асы");
        put("-аша", "аши");

        put("-бье", "бья");

        put("-вец", "вцы");

        put("-дец", "дцы");

        put("-ее", "ие");
        put("-еец", "ейцы,эйцы");
        put("-ей", "еи");
        put("-ел", "елы");
        put("-ен", "ены");
        put("-ень", "ени");
        put("-ер", "еры");
        put("-ерец", "ерцы");
        put("-ес", "есы");
        put("-ец", "ецы,цы");

        put("-иец", "ийцы");
        put("-ив", "ивы");
        put("-ик", "ики");
        put("-им", "имы");
        put("-ин", "ины");
        put("-инец", "ины");

        put("-йец", "ийцы");

        put("-лье", "лья");
        put("-лец", "льцы");

        put("-мат", "маты");
        put("-мец", "мцы");

        put("-нец", "нцы");
        put("-ник", "ники");
        put("-нок", "нки");

        put("-ое", "ые");
        put("-бец", "бцы");
        put("-ой", "ои");
        put("-он", "оны");
        put("-оп", "опы");
        put("-орец", "орцы");
        put("-ос", "осы");
        put("-осс", "оссы");
        put("-осец", "осцы");
        put("-ота", "оты");

        put("-рец", "рцы");
        put("-рка", "рки");
        put("-рок", "рки");
        put("-росс", "россы");
        put("-рус", "русы");
        put("-русс", "руссы");

        put("-сок", "ски");

        put("-ток", "токи");
        put("-тка", "тки");

        put("-уз", "узы");
        put("-ун", "уны");
        put("-ур", "уры");
        put("-урт", "урты");
        put("-ус", "усы");
        put("-уск", "уски");
        put("-ут", "уты");

        put("-чка", "чки");

        put("-цид", "циды");

        put("-шка", "шки");

        put("-щееся", "щиеся");

        put("-ык", "ыки");
        put("-ын", "ыны");
        put("-ыш", "ыши");

        put("-я", "и");
        put("-янин", "яне");
        put("-як", "яки");
        put("-яр", "яры");
        put("-яш", "яши");

    }};

    String replace(String word, String markers) {
        if (markers.contains("нескл.")) {
            return word;
        }

        String exception = EXCEPTIONS.get(word);
        if (exception != null) {
            return exception;
        }

        String alternative = parseAlternative(markers);
        if (alternative.length() > 1 && !alternative.contains("-")) {
            return alternative;
        }

        String lastPart = PLURAL_TO_SINGULAR.get(alternative);
        if (lastPart == null) {
            return null;
        }

        String[] lastParts = lastPart.split(",");
        for (String part : lastParts) {
            if (word.endsWith(part)) {
                word = word.substring(0, word.length() - part.length());
                word = word + alternative.substring(1);
                return word;
            }
        }
        return null;
    }

    private String parseAlternative(String markers) {
        // word: абазины
        // markers: -ин, ед. -инец, -нца, м.
        int singularIndex = markers.indexOf("ед.");
        StringBuilder alternativeBuilder = new StringBuilder();
        boolean braces = false;
        for (int i = singularIndex + "ед.".length(); i < markers.length(); i++) {
            char ch = markers.charAt(i);
            if (ch == '(') {
                braces = true;
                continue;
            }
            if (ch == ')') {
                braces = false;
                continue;
            }
            if (braces) {
                continue;
            }
            if (ch == '-' || Character.isLetter(ch)) {
                alternativeBuilder.append(ch);
                continue;
            }
            if (alternativeBuilder.length() > 0) {
                break;
            }
        }
        return alternativeBuilder.toString();
    }

}
