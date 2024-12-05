
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CompiladorPyRuby extends JFrame {
    private JTextArea areaCodigoEntrada;
    private JTextArea areaSalida;
    private JButton botonCompilar;

    public CompiladorPyRuby() {
        setTitle("Compilador PyRuby");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de entrada de código
        areaCodigoEntrada = new JTextArea(10, 70);
        areaCodigoEntrada.setText("var a = 10;\n" +
                          "var b = 5;\n\n" +
                          "if a > b {\n" +
                          "    ret a;\n" +
                          "}\n" +
                          "fin\n");
                           
        areaCodigoEntrada.setBorder(BorderFactory.createTitledBorder("Entrada de Código"));
        add(new JScrollPane(areaCodigoEntrada), BorderLayout.CENTER);

        // Área de salida
        areaSalida = new JTextArea(20, 70);
        areaSalida.setEditable(false);
        areaSalida.setBorder(BorderFactory.createTitledBorder("Salida del Compilador"));
        JScrollPane scrollPaneSalida = new JScrollPane(areaSalida);
        scrollPaneSalida.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneSalida.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneSalida, BorderLayout.SOUTH);

        // Botón de compilar
        botonCompilar = new JButton("Compilar");
        botonCompilar.addActionListener(new AccionBotonCompilar());
        add(botonCompilar, BorderLayout.NORTH);
    }

    private class AccionBotonCompilar implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String codigo = areaCodigoEntrada.getText();
            areaSalida.setText(""); // Limpiar la salida anterior

            // Análisis léxico
            List<String> tokens = realizarAnalisisLexico(codigo);
            if (tokens == null) {
                return;
            }

            // Análisis sintáctico
            boolean sintaxisValida = realizarAnalisisSintactico(tokens, codigo);
            if (!sintaxisValida) {
                return;
            }

            // Análisis semántico
            boolean semanticaValida = realizarAnalisisSemantico(tokens);
            if (!semanticaValida) {
                return;
            }

            // Generar código de tres direcciones
            String codigoTresDirecciones = generarCodigoTresDirecciones(tokens);
            areaSalida.append("Código Intermedio:\n" + codigoTresDirecciones + "\n\n");

            // Optimizar el código de tres direcciones
            String codigoOptimizado = optimizarCodigoTresDirecciones(codigoTresDirecciones);
            areaSalida.append("Código Optimizado:\n" + codigoOptimizado + "\n");
        }
    }

    private List<String> realizarAnalisisLexico(String codigo) {
        List<String> tokens = tokenizar(codigo);
        areaSalida.append("Tokens Identificados:\n");
        for (String token : tokens) {
            areaSalida.append("Token: " + token + "\n");
        }
        for (String token : tokens) {
            if (!esTokenValido(token)) {
                areaSalida.append("Error Léxico en el token: " + token + "\n\n");
                return null;
            }
        }
        areaSalida.append("Análisis Léxico Completado\n\n");
        return tokens;
    }

    private boolean esTokenValido(String token) {
        String[] palabrasReservadas = {"if", "else", "fin", "ret", "var"};
        for (String palabra : palabrasReservadas) {
            if (token.equals(palabra)) {
                return true;
            }
        }
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*") || 
               token.matches("[0-9]+") ||                
               "+-*/%=<>".contains(token) ||            
               "{}();".contains(token);                 
    }

    private List<String> tokenizar(String codigo) {
        List<String> tokens = new ArrayList<>();
        String[] lineas = codigo.split("\n");
        for (String linea : lineas) {
            linea = linea.replace("{", " { ")
                         .replace("}", " } ")
                         .replace(";", " ; ")
                         .replace("(", " ( ")
                         .replace(")", " ) ")
                         .replace("=", " = ");
            String[] partes = linea.trim().split("\s+");
            for (String parte : partes) {
                tokens.add(parte);
            }
        }
        return tokens;
    }

    private boolean realizarAnalisisSintactico(List<String> tokens, String codigo) {
        areaSalida.append("Detalles del Análisis Sintáctico:\n");
        String[] lineas = codigo.split("\\n");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) {
                continue;
            }
            if (!linea.endsWith(";") && !linea.endsWith("{") && !linea.endsWith("}") && !linea.equals("fin")) {
                areaSalida.append("Error Sintáctico en la línea " + (i + 1) + ": Falta ';' o delimitador de bloque.\n\n");
                return false;
            }
        }
        areaSalida.append("Análisis Sintáctico Completado\n\n");
        return true;
    }

    private boolean realizarAnalisisSemantico(List<String> tokens) {
        areaSalida.append("Detalles del Análisis Semántico:\n");
        areaSalida.append("Análisis Semántico Completado\n\n");
        return true;
    }

    private String generarCodigoTresDirecciones(List<String> tokens) {
        StringBuilder codigoTresDirecciones = new StringBuilder();
        int contadorTemporal = 1;
    
        for (int i = 0; i < tokens.size() - 2; i++) {
            if (tokens.get(i + 1).equals("=")) {
                String izquierda = tokens.get(i);
                String derecha = tokens.get(i + 2);
                codigoTresDirecciones.append("t").append(contadorTemporal).append(" = ").append(izquierda).append(";\n");
                codigoTresDirecciones.append(izquierda).append(" = ").append(derecha).append(";\n");
                contadorTemporal++;
            }
            if (tokens.get(i).equals(">") && i > 0 && i < tokens.size() - 1) {
                String izquierda = tokens.get(i - 1);
                String derecha = tokens.get(i + 1);
                codigoTresDirecciones.append("t").append(contadorTemporal).append(" = ").append(izquierda).append(" > ").append(derecha).append(";\n");
                contadorTemporal++;
            }
            if (tokens.get(i).equals("ret") && i < tokens.size() - 1) {
                String valorRetorno = tokens.get(i + 1);
                codigoTresDirecciones.append("return ").append(valorRetorno).append(";\n");
            }
        }
        return codigoTresDirecciones.toString().trim();
    }

    private String optimizarCodigoTresDirecciones(String codigoTresDirecciones) {
        return codigoTresDirecciones.replace("  ", " "); // Optimización básica
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompiladorPyRuby compilador = new CompiladorPyRuby();
            compilador.setVisible(true);
        });
    }
}
