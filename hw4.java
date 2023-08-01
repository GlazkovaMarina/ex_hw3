/*
 * Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке,
 * разделенные пробелом: Фамилия Имя Отчество, дата_рождения, номер_телефона, пол
 * Форматы данных: фамилия, имя, отчество - строки
 * дата_рождения- строка формата dd.mm.yyyy
 * номер_телефона - целое беззнаковое число без форматирования
 * пол - символ латиницей f или m.
 * Критерии:
 * - Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым,
 * вернуть код ошибки, обработать его и показать пользователю сообщение, что он ввел меньше и больше
 * данных, чем требуется.
 * - Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры.
 * Если форматы данных не совпадают,нужно бросить исключение, соответствующее типу проблемы. Можно
 * использовать встроенные типы java и создать свои. Исключение должно быть корректно обработано,
 * пользователю выведено сообщениес информацией, что именно неверно.
 * - Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в одну
 * строку должны записаться полученные данные, вида.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class hw4 {

    private static boolean isNumber(String s) throws NumberFormatException { // Проверка на целое число
        try {
            int n = Integer.parseInt(s);
            if (n < 0){
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDate(String s){ // Проверка на дату через точки
        String[] date = s.split("\\.");
        for (int i = 0; i < 3; i++) {
            try {
                if (!isNumber(date[i])){
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return false;
            }
        }
        return true;
    }

    private static final String DATE_FORMAT = "dd.MM.yyyy"; // проверка даты на существующую
    private static boolean isValidDate(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isSex(String s){ // Проверка на пол
        if (s.length() == 1){
            if(s.charAt(0) == 'f' || s.charAt(0) == 'm'){
                return true;
            }
        }
        return false;
    }

    private static boolean isString(String s){ // Проверка на строку
        for (int i = 0; i < s.length(); i++) {
            if(((s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') || (s.charAt(i) >= 'a' && s.charAt(i) <= 'z') || (s.charAt(i) >= 'А' && s.charAt(i) <= 'Я') || (s.charAt(i) >= 'а' && s.charAt(i) <= 'я'))){
                ;
            }
            else{
                return false;
            }
        }
        return true;
    }

    public static int inputData(Scanner scanner, String [] data) { // Ввод данных и проверка на правильный ввод
        System.out.println("Введите фамилию имя отчество, дату рождения через точки, номер телефона, пол (f или m): ");
        String line = scanner.nextLine();
        String[] str = line.split(" ");
        boolean [] bool = {false, false, false, false, false, false}; // для проверки неповторяемости данных
        
        if (str.length != 6){ // проверка на кол-во данных
            return str.length;
        }
        else if (!((isString(str[0]) && isString(str[1]) && isString(str[2])) || (isString(str[1]) && isString(str[2]) && isString(str[3])) || (isString(str[2]) && isString(str[3]) && isString(str[4])) ||(isString(str[3]) && isString(str[4]) && isString(str[5])))) // проверка на последовательный ввод фио
        {
            return -1;
        }

        for (int i = 0; i < str.length; i++){ // проверка данных на корректность
            if (isDate(str[i])) // проверка даты
            {   if (!isValidDate(str[i]))
                    return -3;
                if (bool[3] == false){
                    bool[3] = true;
                    data[3] = str[i]; 
                }
                else{
                    return -3;
                }        
            } else if (isNumber(str[i])){ // проверка номера телефона
                if (bool[4] == false){
                    bool[4] = true;
                    data[4] = str[i]; 
                }
                else {
                    return -4;
                }
            } else if (isSex(str[i])){ // проверка пола
                if (bool[5] == false){
                    bool[5] = true;
                    data[5] = str[i]; 
                }
                else {
                    return -5;
                }
            } else if(isString(str[i])){ // проверка фамилии, имени, отчества
                if (bool[0] == false && bool[1] == false && bool[2] == false){
                    bool[0] = true;
                    bool[1] = true;
                    bool[2] = true;
                    data[0] = str[i]; 
                    data[1] = str[i+1];
                    data[2] = str[i+2];
                    i = i + 2; 
                }
                else {
                    return -5;
                }
            } else{ // если номер телефона - знаковое целое
                return -4;
            }
        }
        for (boolean b : bool) { //проверка на ввод всех данных по одному разу
            if (b == false){
                return -6;
            }
        }

        return 6; // все корректно
    }

    public static int writeFile(String[] data){ // дозапись в файл
        File file = new File(data[0]+".txt");
        BufferedWriter bf = null;
        try {
            FileWriter fr = new FileWriter(file, true); // открытие на дозапись
            bf = new BufferedWriter(fr);
            for (String item : data) {
                bf.write(item + " ");  
            }
            bf.newLine();
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + file.getName());
            return -1;
        } catch (IOException e){
            System.out.println(e.getMessage());
            return -1;
        } finally{
            if(bf != null){
                try{
                    bf.close();
                } catch(IOException e){
                    System.out.println("Exception while close");
                }        
            }
        }
        return 1;
    }

    public static void User(Scanner scanner){ // Вывод о корректности введенных данных
        int in;
        String [] data = new String[6]; // фамилия, имя, отчество, дата рождения через точки, номер телефона, пол (f или m)
        do {
            in = 3;
            int code = inputData(scanner, data);
            if (code == 6){ // если все данные корректные
                int key = writeFile(data);
                if (key == 1){
                    System.out.println("Данные сохранены в файл под вашей фамилией.");
                } else{
                    System.out.println("Из-за проблем с файлом не удалось сохранить данные введенного пользователя!");
                }
            } else if (code == -1){
                System.out.println("Некорректный ввод данных. Последовательно введите фамилию, имя и отчество через прробел!");
            } else if (code == -2){
                System.out.println("Некорректный ввод данных про фио!");
            } else if (code == -3){
                System.out.println("Некорректный ввод данных про дату рождения!");
            } else if (code == -4){
                System.out.println("Некорректный ввод данных про номер телефона!");
            } else if (code == -5){
                System.out.println("Некорректный ввод данных про пол!");
            } else if (code == -6){
                System.out.println("Некорректный ввод данных!");
            }else if (code > 6){
                System.out.println("Некорректный ввод данных. Количество данных превышено!");
            } else if (code >= 0 && code < 6){
                System.out.println("Некорректный ввод данных. Недостаточно данных!!");
            }
            System.out.println("Хотите добавить информацию еще про одного человека? Если да - нажмите 1, иначе - 2");
            
            while (in != 1 && in != 2){ // проверка на корректный ввод
                String line = scanner.nextLine();
                try {
                    in = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    System.out.println("Введенное значение нельзя преобразовать к числу!");
                }
                if (in != 1 && in != 2){
                    System.out.println("Введите 1 или 2!");
                }
            }
        } while (in == 1);
    }
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)){ // открывает единожды входной поток с гарантированным автоматическим закрытием
            User(scanner);
        }
    }
}