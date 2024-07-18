import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Classe principal da clínica
public class ClinicaCDI {
    // Listas para armazenar pacientes e atendimentos
    private static List<Paciente> pacientes = new ArrayList<>();
    private static List<Atendimento> atendimentos = new ArrayList<>();
    // Scanner para entrada do usuário
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Exibe o menu principal
        exibirMenu();
    }

    // Exibe o menu principal e processa a escolha do usuário
    private static void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Cadastrar Paciente");
            System.out.println("2. Realizar Atendimento");
            System.out.println("3. Listar Pacientes");
            System.out.println("4. Listar Atendimentos em uma Data");
            System.out.println("5. Número de Procedimentos em um Período");
            System.out.println("6. Tempo Total de Duração de Procedimento em um Período");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            switch (opcao) {
                case 1:
                    cadastrarPaciente();
                    break;
                case 2:
                    realizarAtendimento();
                    break;
                case 3:
                    listarPacientes();
                    break;
                case 4:
                    listarAtendimentosPorData();
                    break;
                case 5:
                    numeroProcedimentosPeriodo();
                    break;
                case 6:
                    tempoTotalProcedimentoPeriodo();
                    break;
                case 7:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 7);
    }

    // Método para cadastrar um novo paciente
    private static void cadastrarPaciente() {
        System.out.println("\n=== Cadastro de Paciente ===");
        System.out.print("Nome Completo: ");
        String nomeCompleto = scanner.nextLine();

        System.out.print("Nome da Mãe: ");
        String nomeMae = scanner.nextLine();

        System.out.print("Data de Nascimento (dd/MM/yyyy): ");
        LocalDate dataNascimento = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.print("Sexo (M/F): ");
        String sexo = scanner.nextLine();

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        Paciente paciente = new Paciente(nomeCompleto, nomeMae, dataNascimento, sexo, cpf);

        // Verifica se o paciente já está cadastrado
        if (pacientes.contains(paciente)) {
            System.out.println("Paciente já cadastrado.");
        } else {
            pacientes.add(paciente);
            System.out.println("Paciente cadastrado com sucesso.");
        }
    }

    // Método para realizar um atendimento
    private static void realizarAtendimento() {
        if (pacientes.isEmpty()) {
            System.out.println("Não há pacientes cadastrados.");
            return;
        }

        System.out.println("\n=== Realizar Atendimento ===");
        System.out.print("CPF do Paciente: ");
        String cpf = scanner.nextLine();

        // Busca o paciente pelo CPF
        Paciente paciente = buscarPacientePorCPF(cpf);

        if (paciente == null) {
            System.out.println("Paciente não encontrado.");
            return;
        }

        System.out.println("Procedimentos disponíveis:");
        System.out.println("1. Raio-X de Tórax em PA");
        System.out.println("2. Ultrassonografia Obstétrica");
        System.out.println("3. Ultrassonografia de Próstata");
        System.out.println("4. Tomografia");

        System.out.print("Escolha o procedimento (1-4): ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        String procedimento;
        switch (escolha) {
            case 1:
                procedimento = "Raio-X";
                break;
            case 2:
                procedimento = "Ultrassonografia Obstétrica";
                break;
            case 3:
                procedimento = "Ultrassonografia de Próstata";
                break;
            case 4:
                procedimento = "Tomografia";
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }

        // Valida se o procedimento pode ser realizado no paciente
        if (!validarProcedimento(paciente, procedimento)) {
            System.out.println("Não é possível realizar este procedimento para o paciente.");
            return;
        }

        // Registra o atendimento
        Atendimento atendimento = new Atendimento(paciente, LocalDate.now(), procedimento);
        atendimentos.add(atendimento);
        System.out.println("Atendimento registrado com sucesso.");
    }

    // Método para buscar um paciente pelo CPF
    private static Paciente buscarPacientePorCPF(String cpf) {
        for (Paciente paciente : pacientes) {
            if (paciente.getCpf().equals(cpf)) {
                return paciente;
            }
        }
        return null;
    }

    // Método para validar se o procedimento pode ser realizado no paciente
    private static boolean validarProcedimento(Paciente paciente, String procedimento) {
        switch (procedimento) {
            case "Raio-X":
                return true; // Todos os pacientes podem fazer Raio-X
            case "Ultrassonografia Obstétrica":
                return paciente.getSexo().equalsIgnoreCase("F") && paciente.getIdade() < 60;
            case "Ultrassonografia de Próstata":
                return paciente.getSexo().equalsIgnoreCase("M");
            case "Tomografia":
                // Verifica se o paciente não fez Ultrassonografia Obstétrica ou Próstata nos últimos três meses
                return !realizouProcedimentoRecente(paciente, "Ultrassonografia Obstétrica") &&
                       !realizouProcedimentoRecente(paciente, "Ultrassonografia de Próstata");
            default:
                return false;
        }
    }

    // Método para verificar se o paciente realizou um procedimento recentemente
    private static boolean realizouProcedimentoRecente(Paciente paciente, String procedimento) {
        LocalDate hoje = LocalDate.now();
        LocalDate tresMesesAtras = hoje.minusMonths(3);

        for (Atendimento atendimento : atendimentos) {
            if (atendimento.getPaciente().equals(paciente) &&
                atendimento.getProcedimento().equals(procedimento) &&
                atendimento.getDataAtendimento().isAfter(tresMesesAtras)) {
                return true;
            }
        }
        return false;
    }

    // Método para listar todos os pacientes cadastrados
    private static void listarPacientes() {
        System.out.println("\n=== Lista de Pacientes ===");
        for (Paciente paciente : pacientes) {
            System.out.printf("Nome: %s, Data de Nascimento: %s\n",
                    paciente.getNomeCompleto(), paciente.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    // Método para listar atendimentos por data
    private static void listarAtendimentosPorData() {
        System.out.print("\nData para listar os atendimentos (dd/MM/yyyy): ");
        LocalDate data = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.println("\n=== Atendimentos realizados em " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ===");
        for (Atendimento atendimento : atendimentos) {
            if (atendimento.getDataAtendimento().equals(data)) {
                System.out.printf("Paciente: %s, Procedimento: %s\n",
                        atendimento.getPaciente().getNomeCompleto(), atendimento.getProcedimento());
            }
        }
    }

    // Método para contar o número de procedimentos em um período
    private static void numeroProcedimentosPeriodo() {
        System.out.print("\nPeríodo para contar os procedimentos (dd/MM/yyyy - dd/MM/yyyy): ");
        String periodoStr = scanner.nextLine();
        String[] partes = periodoStr.split(" - ");

        LocalDate inicio = LocalDate.parse(partes[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate fim = LocalDate.parse(partes[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        int contadorRaioX = 0, contadorUSObstetrica = 0, contadorUSProstata = 0, contadorTomografia = 0;

        for (Atendimento atendimento : atendimentos) {
            LocalDate dataAtendimento = atendimento.getDataAtendimento();
            if (!dataAtendimento.isBefore(inicio) && !dataAtendimento.isAfter(fim)) {
                switch (atendimento.getProcedimento()) {
                    case "Raio-X":
                        contadorRaioX++;
                        break;
                    case "Ultrassonografia Obstétrica":
                        contadorUSObstetrica++;
                        break;
                    case "Ultrassonografia de Próstata":
                        contadorUSProstata++;
                        break;
                    case "Tomografia":
                        contadorTomografia++;
                        break;
                }
            }
        }

        System.out.printf("\nNúmero de procedimentos no período:\n");
        System.out.printf("Raio-X: %d\n", contadorRaioX);
        System.out.printf("Ultrassonografia Obstétrica: %d\n", contadorUSObstetrica);
        System.out.printf("Ultrassonografia de Próstata: %d\n", contadorUSProstata);
        System.out.printf("Tomografia: %d\n", contadorTomografia);
    }

    // Método para calcular o tempo total dos procedimentos em um período
    private static void tempoTotalProcedimentoPeriodo() {
        System.out.print("\nPeríodo para calcular o tempo total dos procedimentos (dd/MM/yyyy - dd/MM/yyyy): ");
        String periodoStr = scanner.nextLine();
        String[] partes = periodoStr.split(" - ");

        LocalDate inicio = LocalDate.parse(partes[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate fim = LocalDate.parse(partes[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        int tempoTotalRaioX = 0, tempoTotalUSObstetrica = 0, tempoTotalUSProstata = 0, tempoTotalTomografia = 0;

        for (Atendimento atendimento : atendimentos) {
            LocalDate dataAtendimento = atendimento.getDataAtendimento();
            if (!dataAtendimento.isBefore(inicio) && !dataAtendimento.isAfter(fim)) {
                switch (atendimento.getProcedimento()) {
                    case "Raio-X":
                        tempoTotalRaioX += 15; // Exemplo de tempo de duração para Raio-X
                        break;
                    case "Ultrassonografia Obstétrica":
                        tempoTotalUSObstetrica += 30; // Exemplo de tempo de duração para Ultrassonografia Obstétrica
                        break;
                    case "Ultrassonografia de Próstata":
                        tempoTotalUSProstata += 30; // Exemplo de tempo de duração para Ultrassonografia de Próstata
                        break;
                    case "Tomografia":
                        tempoTotalTomografia += 60; // Exemplo de tempo de duração para Tomografia
                        break;
                }
            }
        }

        System.out.printf("\nTempo total de procedimentos no período:\n");
        System.out.printf("Raio-X: %d minutos\n", tempoTotalRaioX);
        System.out.printf("Ultrassonografia Obstétrica: %d minutos\n", tempoTotalUSObstetrica);
        System.out.printf("Ultrassonografia de Próstata: %d minutos\n", tempoTotalUSProstata);
        System.out.printf("Tomografia: %d minutos\n", tempoTotalTomografia);
    }
}

// Classe Paciente
class Paciente {
    private String nomeCompleto;
    private String nomeMae;
    private LocalDate dataNascimento;
    private String sexo;
    private String cpf;

    public Paciente(String nomeCompleto, String nomeMae, LocalDate dataNascimento, String sexo, String cpf) {
        this.nomeCompleto = nomeCompleto;
        this.nomeMae = nomeMae;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.cpf = cpf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public String getSexo() {
        return sexo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return cpf.equals(paciente.cpf);
    }

    @Override
    public int hashCode() {
        return cpf.hashCode();
    }

    // Método para calcular a idade do paciente
    public int getIdade() {
        return LocalDate.now().getYear() - dataNascimento.getYear();
    }
}

// Classe Atendimento
class Atendimento {
    private Paciente paciente;
    private LocalDate dataAtendimento;
    private String procedimento;

    public Atendimento(Paciente paciente, LocalDate dataAtendimento, String procedimento) {
        this.paciente = paciente;
        this.dataAtendimento = dataAtendimento;
        this.procedimento = procedimento;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public String getProcedimento() {
        return procedimento;
    }
}