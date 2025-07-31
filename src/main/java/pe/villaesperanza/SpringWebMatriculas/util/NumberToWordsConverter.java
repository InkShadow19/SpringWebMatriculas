package pe.villaesperanza.SpringWebMatriculas.util;

import org.springframework.stereotype.Component;
import java.text.DecimalFormat;

@Component
public class NumberToWordsConverter {

    private final String[] UNIDADES = { "", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE " };
    private final String[] DECENAS = { "DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ", "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA " };
    private final String[] CENTENAS = { "", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS " };

    public String convertToWords(Double numero) {
        if (numero == null) return "CERO CON 00/100 SOLES";
        
        String literal;
        String parte_decimal;
        
        DecimalFormat df = new DecimalFormat("#.00");
        String numeroStr = df.format(numero).replace(",", ".");
        
        String[] num = numeroStr.split("\\.");

        if (Integer.parseInt(num[0]) == 0) {
            literal = "CERO ";
        } else if (Integer.parseInt(num[0]) > 999999) {
            literal = getMillones(num[0]);
        } else if (Integer.parseInt(num[0]) > 999) {
            literal = getMiles(num[0]);
        } else if (Integer.parseInt(num[0]) > 99) {
            literal = getCentenas(num[0]);
        } else if (Integer.parseInt(num[0]) > 9) {
            literal = getDecenas(num[0]);
        } else {
            literal = getUnidades(num[0]);
        }
        
        parte_decimal = "CON " + num[1] + "/100 SOLES";

        return (literal + parte_decimal).toUpperCase();
    }

    private String getUnidades(String numero) {
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private String getDecenas(String num) {
        int n = Integer.parseInt(num);
        if (n < 10) {
            return getUnidades(num);
        } else if (n > 19) {
            String u = getUnidades(num);
            if (u.equals("")) {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8];
            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8] + "Y " + u;
            }
        } else {
            return DECENAS[n - 10];
        }
    }

    private String getCentenas(String num) {
        if (Integer.parseInt(num) > 99) {
            if (Integer.parseInt(num) == 100) {
                return "CIEN ";
            } else {
                return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }
        } else {
            return getDecenas(Integer.parseInt(num) + "");
        }
    }

    private String getMiles(String numero) {
        String c = numero.substring(numero.length() - 3);
        String m = numero.substring(0, numero.length() - 3);
        String n;
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "MIL " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }
    }

    private String getMillones(String numero) {
        String miles = numero.substring(numero.length() - 6);
        String millon = numero.substring(0, numero.length() - 6);
        String n;
        if (millon.length() > 1) {
            n = getCentenas(millon) + "MILLONES ";
        } else {
            n = getUnidades(millon) + "MILLON ";
        }
        return n + getMiles(miles);
    }
}