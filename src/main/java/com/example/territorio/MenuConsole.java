package com.example.territorio;

import com.example.territorio.dominio.Alerta;
import com.example.territorio.dominio.DadosTerritorio;
import com.example.territorio.dominio.Limiares;
import com.example.territorio.dominio.Medicao;
import com.example.territorio.dominio.Sensor;
import com.example.territorio.excecao.DadosInvalidosException;
import com.example.territorio.excecao.FonteIndisponivelException;
import com.example.territorio.servico.RelatorioBuilder;
import com.example.territorio.servico.ResultadoPrevisao;
import com.example.territorio.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Menu de console. Só fala com o serviço e mostra mensagens claras ao usuário. */
@Component
public class MenuConsole implements CommandLineRunner {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ServicoMonitoramento servico;
    private final Scanner in = new Scanner(System.in);

    public MenuConsole(ServicoMonitoramento servico) {
        this.servico = servico;
    }

    @Override
    public void run(String... args) {
        boolean rodando = true;
        while (rodando && in.hasNextLine()) {
            imprimirMenu();
            String opcao = in.nextLine().trim();
            try {
                switch (opcao) {
                    case "1" -> lerSensores();
                    case "2" -> rodarPrevisoes();
                    case "3" -> verHistorico();
                    case "4" -> verAlertas();
                    case "5" -> gerenciarTerritorio();
                    case "6" -> mostrarStatus();
                    case "0" -> rodando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("Dados inválidos: " + e.getMessage());
            }
        }
        System.out.println("Sistema encerrado.");
    }

    private void imprimirMenu() {
        System.out.println();
        System.out.println("=== Monitoramento de calor e energia ===");
        System.out.println("1) Verificar sensores (ler e gravar)");
        System.out.println("2) Rodar previsões");
        System.out.println("3) Ver histórico por sensor");
        System.out.println("4) Ver alertas emitidos");
        System.out.println("5) Gerenciar dados do território");
        System.out.println("6) Status completo");
        System.out.println("0) Encerrar");
    }

    private void lerSensores() {
        for (Sensor sensor : servico.getSensores()) {
            try {
                var medicao = servico.lerSensor(sensor);
                System.out.printf("%s: %.1f %s (gravado)%n", sensor.getId(), medicao.getValor(), sensor.getUnidade());
            } catch (FonteIndisponivelException | DadosInvalidosException e) {
                System.out.printf("%s: %s%n", sensor.getId(), e.getMessage());
            }
        }
    }

    private void rodarPrevisoes() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        String escolha = ler("Estratégia (1 = média móvel, 2 = comparação entre períodos): ");
        String chave = "2".equals(escolha) ? "COMPARACAO" : "MEDIA";

        ResultadoPrevisao resultado = servico.gerarPrevisao(sensor, chave);
        System.out.print(new RelatorioBuilder().titulo("Previsão").resultado(resultado).construir());
    }

    private void verHistorico() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        List<Medicao> historico = servico.historico(sensor.getId());
        if (historico.isEmpty()) {
            System.out.println("Nenhuma leitura gravada para " + sensor.getId() + ".");
            return;
        }
        for (Medicao m : historico) {
            System.out.printf("%s | %.1f %s%n", m.getMomento().format(FORMATO_DATA), m.getValor(), m.getUnidade());
        }
    }

    private void verAlertas() {
        List<Alerta> alertas = servico.alertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido ainda.");
            return;
        }
        for (Alerta a : alertas) {
            System.out.printf("%s | %s | %s%n", a.getMomento().format(FORMATO_DATA), a.getNivel(), a.getExplicacao());
        }
    }

    private void gerenciarTerritorio() throws DadosInvalidosException {
        System.out.println("a) Ver dados do território");
        System.out.println("b) Alterar limiares (exige código de acesso)");
        System.out.println("c) Registrar leitura manual");
        System.out.println("d) Simular defeito em um sensor (liga/desliga)");
        String opcao = ler("Escolha: ");

        switch (opcao.toLowerCase(Locale.ROOT)) {
            case "a" -> mostrarTerritorio();
            case "b" -> alterarLimiares();
            case "c" -> registrarLeituraManual();
            case "d" -> alternarDefeito();
            default -> System.out.println("Opção inválida.");
        }
    }

    private void mostrarTerritorio() {
        DadosTerritorio t = servico.getTerritorio();
        System.out.println("Território: " + t.getNome() + " (" + t.getRegiao() + ")");
        servico.getSetores().forEach(s -> System.out.println("  " + s.verificarStatus()));
    }

    private void alterarLimiares() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        String codigo = ler("Código de acesso: ");
        double atencao = lerNumero("Limiar de ATENÇÃO: ");
        double alerta = lerNumero("Limiar de ALERTA: ");
        double critico = lerNumero("Limiar de CRÍTICO: ");

        servico.alterarLimiares(codigo, sensor.getTipo(), new Limiares(atencao, alerta, critico));
        System.out.println("Limiares atualizados para " + sensor.getTipo() + ".");
    }

    private void registrarLeituraManual() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        double valor = lerNumero("Valor (" + sensor.getUnidade() + "): ");
        Medicao m = servico.registrarLeitura(sensor, valor);
        System.out.printf("Leitura gravada: %.1f %s%n", m.getValor(), m.getUnidade());
    }

    private void alternarDefeito() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        boolean novoEstado = sensor.verificarFuncionamento(); // se funciona, vira defeito
        sensor.definirDefeito(novoEstado);
        System.out.println("Sensor " + sensor.getId() + (novoEstado ? " com defeito simulado." : " funcionando."));
    }

    private void mostrarStatus() {
        servico.statusCompleto().forEach(System.out::println);
    }

    private Sensor escolherSensor() throws DadosInvalidosException {
        List<Sensor> sensores = servico.getSensores();
        for (int i = 0; i < sensores.size(); i++) {
            System.out.printf("  %d) %s - %s%n", i + 1, sensores.get(i).getId(), sensores.get(i).getTipo());
        }
        int numero = lerInteiro("Escolha o sensor: ");
        if (numero < 1 || numero > sensores.size()) {
            throw new DadosInvalidosException("Sensor inexistente: " + numero);
        }
        return sensores.get(numero - 1);
    }

    private String ler(String pergunta) {
        System.out.print(pergunta);
        return in.nextLine().trim();
    }

    private int lerInteiro(String pergunta) throws DadosInvalidosException {
        try {
            return Integer.parseInt(ler(pergunta));
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Digite um número inteiro.");
        }
    }

    private double lerNumero(String pergunta) throws DadosInvalidosException {
        try {
            return Double.parseDouble(ler(pergunta).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Digite um número válido.");
        }
    }
}
