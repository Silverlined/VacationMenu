import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VacationMenu {
    static File infoVacations = new File("DataVacations.txt");
    static Scanner input = new Scanner(System.in);
    static int counter;
    static Random random = new Random();
    static List<Integer> numbersSoFar = new ArrayList<>();
    static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public static void main(String[] args) throws IOException, ParseException {
        mainMenu();
    }

    private static void mainMenu() throws IOException, ParseException {
        System.out.println("---------------------------------------------");
        System.out.print("1. Заяви отпуска\n2. Виж всички отпуски\n3. Виж отпуска на служител\n4. Промени статус на отпуска\n5. Изход\n");
        System.out.println("---------------------------------------------");
        System.out.print("Въведи избор: ");
        try {
            byte chosenOption = input.nextByte();
            switch (chosenOption) {
                case 1:
                    menuVacationInquiry();
                    break;
                case 2:
                    showAllInquiriesMenu();
                    break;
                case 3:
                    showEmployeeInquiries();
                    break;
                case 4:
                    System.out.println("Password:     (hint. Best Project)");
                    input.nextLine();
                    if (input.nextLine().equals("Best Project")) {
                        System.out.println("Access granted. You can change the Status of the inquiries.\n");
                        subMenu4ChangeStatus();
                    } else {
                        System.out.println("Incorrect password, access denied.");
                    }
                    break;
                case 5:
                    System.out.println("Have a wonderful day :)");
                    System.exit(0);
                default:
                    System.out.println("Incorrect number, please restart and select option 1 - 5");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Невалиден избор, моля рестартирайте и изберете опция от 1 до 5.");
        }
    }

    private static void menuVacationInquiry() throws IOException, ParseException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(infoVacations, true),
                StandardCharsets.UTF_8));
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

        System.out.println("---------------------------------------------");
        writer.newLine();
        System.out.println("Име: ");
        input.nextLine();
        getName(writer);
        System.out.println("Email: ");
        getEmail(writer);
        System.out.println("ЕГН: ");
        getEGN(writer);
        System.out.println("Начало на отпуската (date/month/year): ");
        Date todayDate = getDate(dateFormat);
        while (true) {
            try {
                Date startDate = dateFormat.parse(input.nextLine());
                if (startDate.after(todayDate)) {
                    writer.write(dateFormat.format(startDate));
                    writer.newLine();
                    todayDate = startDate;
                    break;
                } else {
                    System.out.println("Невалидна начална дата. Моля, въведете нова дата, след днешната. Format(dd/mm/yyyy)");
                }
            } catch (ParseException e) {
                System.out.println("Невалидна начална дата. Моля, въведете нова дата. Format(dd/mm/yyyy)");
            }
        }
        System.out.println("Край на отпуската (date/month/year): ");
        while (true) {
            try {
                Date endDate = dateFormat.parse(input.nextLine());
                if (endDate.after(todayDate)) {
                    writer.write(dateFormat.format(endDate));
                    writer.newLine();
                    break;
                } else {
                    System.out.println("Невалидна крайна дата. Моля, въведете нова дата, след началната. Format(dd/mm/yyyy)");
                }
            } catch (ParseException e) {
                System.out.println("Невалидна крайна дата. Моля, въведете нова дата. Format(dd/mm/yyyy)");
            }
        }
        System.out.println("Тип на отпуската (платена/неплатена): ");
        getTypeVacation(writer);
        writer.write("pending");
        writer.newLine();
        writer.write(String.valueOf(generateRandomNumber(random)));
        writer.newLine();
        System.out.println("---------------------------------------------");
        writer.flush();
        writer.close();

        System.out.print("Вашата заявка за отпуска е записана.");
        returnToMainMenu();
    }

    private static void showAllInquiriesMenu() throws IOException, ParseException {
        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(infoVacations), "UTF8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("There is no file with inquiries");
            System.exit(1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Sorry, your system does not support the encoding of the data");
            System.exit(1);
        }
        System.out.println("Table of all vacation inquiries up to this moment :)");
        System.out.print("\t\tИме:       \t\t\tЕ-mail:    \t\t    ЕГН:   \t\t\t\tНачало:    \t\t\tКрай:  \t\t\t\tТип:    \t\t\tСтатус:\n");
        while (bReader.readLine() != null) {
            for (int j = 0; j < 8; j++) {
                if (j != 7) {
                    System.out.printf("\t\t%-15s", bReader.readLine());
                } else bReader.readLine();
            }
            System.out.println();
        }
        if (bReader != null) {
            bReader.close();
        }
        returnToMainMenu();
    }

    private static void showEmployeeInquiries() throws IOException, ParseException {
        String[] employeesInquiries = new String[100];
        packAllInquiriesInArray(employeesInquiries);
        System.out.print("Въведете име на служител: ");
        input.nextLine();
        String nameEmployee = input.nextLine();
        System.out.println();
        try {
            for (String i : employeesInquiries) {
                if (i.equals(nameEmployee)) {
                    System.out.print("\t\tИме:       \t\t\tЕ-mail:    \t\t    ЕГН:   \t\t\t\tНачало:    \t\t\tКрай:  \t\t\t\tТип:    \t\t\tСтатус:\n");
                    break;
                }
            }
            for (int k = 1; k <= counter; k += 9) {
                if (employeesInquiries[k].equals(nameEmployee)) {
                    for (int j = 0; j < 8; j++) {
                        if (j != 7) {
                            System.out.printf("\t\t%-15s", employeesInquiries[k + j]);
                        } else {
                            continue;
                        }
                    }
                    System.out.println();
                }
            }
        } catch (NullPointerException e) {
            System.out.println("There are no inquiries related to this name.");
        }
        returnToMainMenu();
    }

    private static void subMenu4ChangeStatus() throws IOException, ParseException {
        String[] employeesInquiries = new String[100];
        packAllInquiriesInArray(employeesInquiries);
        System.out.print("\t\tИме:       \t\t\tЕ-mail:    \t\t    ЕГН:   \t\t\t\tНачало:    \t\t\tКрай:  \t\t\t\tТип:    \t\t\tСтатус:    \t\t\tНомер:\n");
        for (int k = 1; k <= counter; k++) {
            if (k % 9 == 0) {
                System.out.println();
            } else {
                System.out.printf("\t\t%-15s", employeesInquiries[k]);
            }
        }
        System.out.println("\nВъведете номер на заявка за да я промените.");
        String numberInquiry = input.nextLine();
        for (int i = 8; i <= counter; i += 9) {
            if (numberInquiry.equals(employeesInquiries[i])) {
                System.out.println("Set the status to approved or rejected:");
                while (true) {
                    String temp = (input.nextLine()).toLowerCase();
                    if (temp.equals("approved") || temp.equals("rejected")) {
                        employeesInquiries[i - 1] = temp;
                        writeChangedInquiries(employeesInquiries);
                        System.out.println("If you would like to change another inquiry, please enter 2. Otherwise enter any key to exit");
                        if (input.nextLine().equals("2")) {
                            subMenu4ChangeStatus();
                        } else {
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Please, enter either approved or rejected.");
                    }
                }
            }
        }
        System.out.println("Invalid number.");
        returnToMainMenu();
    }


    private static void getName(BufferedWriter writer) throws IOException {
        while (true) {
            String temp = (input.nextLine()).trim();
            if (!temp.isEmpty() && !(temp == null)) {
                writer.write(temp);
                writer.newLine();
                break;
            } else {
                System.out.println("Невалидно име. Моля, опитайте отново.");
            }
        }
    }

    private static void getTypeVacation(BufferedWriter writer) throws IOException {
        while (true) {
            String temp = input.nextLine();
            if (temp.equalsIgnoreCase("платена") || temp.equalsIgnoreCase("неплатена")) {
                writer.write(temp);
                writer.newLine();
                break;
            } else {
                System.out.println("Невалиден тип отпуска. Моля, въведете \"платена/неплатена\".");
            }
        }
    }

    private static void getEGN(BufferedWriter writer) throws IOException {
        while (true) {
            try {
                String tempEGN = input.nextLine();
                tempEGN.trim();
                long check = Long.parseLong(tempEGN);
                if (tempEGN.length() == 10) {
                    writer.write(tempEGN);
                    writer.newLine();
                    break;
                } else {
                    System.out.println("Невалидно ЕГН. Моля, въведете десетцифрено число.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Невалидно ЕГН. Моля, въведете десетцифрено число.");
                continue;
            }
        }
    }

    private static void getEmail(BufferedWriter writer) throws IOException {
        while (true) {
            String email = input.nextLine();
            email.trim();
            if (validateEmail(email)) {
                writer.write(email);
                writer.newLine();
                break;
            } else {
                System.out.println("Невалиден Емайл. Моля, въведете нов.");
            }
        }
    }

    private static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private static Date getDate(DateFormat dateFormat) throws ParseException {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH) + 1;
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);
        return dateFormat.parse(day + "/" + month + "/" + year);
    }

    private static String[] packAllInquiriesInArray(String[] employeesInquiries) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(infoVacations, "UTF-8");
        } catch (FileNotFoundException e) {
            System.out.println("There is no file with vacation inquiries.");
        }
        counter = 0;
        while (scanner.hasNextLine()) {
            employeesInquiries[counter] = scanner.nextLine();
            counter++;
        }
        if (scanner != null) {
            scanner.close();
        }
        return employeesInquiries;
    }

    private static void writeChangedInquiries(String[] inquiries) throws FileNotFoundException, UnsupportedEncodingException {
        PrintStream writer = new PrintStream(infoVacations, "UTF-8");
        for (int i = 0; i < counter; i++) {
            writer.println(inquiries[i]);
        }
    }

    private static void returnToMainMenu() throws IOException, ParseException {
        System.out.println("\nАко искате да се върнете към главното меню, моля изберете 1. В противен случай натиснете някой бутон за изход.");
        if (input.next().equals("1")) {
            mainMenu();
        } else {
            System.exit(0);
        }
    }

    private static int generateRandomNumber(Random rand) {
        int temp = rand.nextInt(8999) + 1000;
        for (int i : numbersSoFar) {
            if (temp == i) {
                generateRandomNumber(random);
            }
        }
        numbersSoFar.add(temp);
        return temp;
    }
}