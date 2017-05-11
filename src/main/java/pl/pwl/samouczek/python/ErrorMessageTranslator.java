package pl.pwl.samouczek.python;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorMessageTranslator {
	
	public static String translateError(String errorMessage) {
		if (errorMessage != null) {
			if (errorMessage.startsWith("cannot concatenate 'str' and") ||
				(errorMessage.startsWith("unsupported operand type(s) for +:") && errorMessage.endsWith("and 'str'"))) {
				return "Do napisu można dokleić tylko inny napis - jeśli chcesz wymusić zamianę zmiennej innego typu na napis, użyj funkcji str, np. str(x)";
			} else if (errorMessage.startsWith("(&quot;no viable alternative at input")) {
				return "Zbyt długie polecenie - na końcu znajdują się nadmiarowe elementy.";
			} else if (Pattern.compile("name '[A-Za-z_][A-Za-z0-9_]*' is not defined").matcher(errorMessage).matches()) {
				Matcher matcher = Pattern.compile("name '([A-Za-z_][A-Za-z0-9_]*)' is not defined").matcher(errorMessage);
				if (matcher.matches()) {
					String varName = matcher.group(1);
					return "Nie znaleziono zmiennej o nazwie: " + varName + ". Aby zamiast do zmiennej odwołać się do napisu o takiej zawartości, należy umieścić go w apostrofach.";
				}
			} else if (errorMessage.contains("expecting COLON")) {
				return "Zapomniałeś/aś dwukropka po instrukcji rozpoczynającej pętlę/funkcję/warunek.";
			}
		}
		return "Błąd przy wykonywaniu polecenia: " + errorMessage;
	}
	
}
