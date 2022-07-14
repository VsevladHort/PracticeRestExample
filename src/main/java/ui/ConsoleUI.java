package ui;

import entities.Good;
import entities.GoodGroup;
import global_utils.Const;
import rest_api.client.RestApiClient;
import rest_api.server.RestHttpsServer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private RestApiClient client;
    private boolean running = true;
    private Scanner scanner;

    public ConsoleUI() {
        client = new RestApiClient();
        scanner = new Scanner(System.in);
    }

    public static void main(String... args) throws IOException, NoSuchAlgorithmException {
        ConsoleUI consoleUI = new ConsoleUI();
        RestHttpsServer restHttpsServer = new RestHttpsServer(Const.MAIN_DB);
        restHttpsServer.start();
        consoleUI.runClient();
    }

    public void runClient() throws IOException, NoSuchAlgorithmException {
        while (running) {
            System.out.print("""
                    To work with Groups press 1,
                    To work with Goods press 2,
                    To shutdown client and request server shutdown press 3: 
                    """);
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                workWithGroups();
            } else if (choice.equals("2")) {
                workWithGoods();
            } else if (choice.equals("3")) {
                running = false;
                client.requestServerShutdown();
            } else
                System.out.println("Illegal choice was maid");
        }
    }

    private void workWithGoods() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            System.out.print("""
                    To create a good press 1,
                    To edit a good press 2,
                    To delete a good press 3,
                    To display all goods press 4,
                    To display the total price of all goods of a given type 5,
                    To search for good press 6,                                       
                    To go back press 7:                                              
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createGoodDialogue();
                case "2" -> editGoodDialogue();
                case "3" -> deleteGoodDialogue();
                case "4" -> displayGoodsDialogue();
                case "5" -> totalValueOfGoodDialogue();
                case "6" -> searchForGooDialogue();
                case "7" -> thisMenu = false;
                default -> System.out.println("Illegal choice was maid");
            }
        }
    }

    private void searchForGooDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            System.out.print("Enter name of the good: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                System.out.println("Empty names are forbidden");
                continue;
            }
            Good good = client.getGood(name);
            if (good != null) {
                System.out.println("Success! The good: ");
                System.out.println(good);
            } else {
                System.out.println("Good by this name was not found!");
            }
            System.out.print("Try searching again?(Y/N): ");
            String yn = scanner.nextLine();
            if (yn.equals("N"))
                thisMenu = false;
        }
    }

    private void totalValueOfGoodDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            List<Good> list = client.getGoods(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no goods to display the total price of!");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the good to display the total price of: ");
            int index;
            String integer = scanner.nextLine();
            try {
                index = Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            Good good = list.get(index);
            double totalValue = good.getAmount() * good.getPrice();
            System.out.println("Total value of the product " + good.getName() + " is " + totalValue);
            System.out.print("Calculate for another good?(Y/N): ");
            String yn = scanner.nextLine();
            if (yn.equals("N"))
                thisMenu = false;
        }
    }

    private void displayGoodsDialogue() throws IOException, NoSuchAlgorithmException {
        List<Good> list = client.getGoods(Integer.MAX_VALUE);
        if (list.isEmpty()) {
            System.out.println("There are no goods to display!");
            return;
        }
        for (int i = 0; i < list.size(); i++)
            System.out.println("" + i + ". " + list.get(i));
    }

    private void deleteGoodDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            List<Good> list = client.getGoods(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no goods to delete!");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the good to delete: ");
            int index;
            String integer = scanner.nextLine();
            try {
                index = Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            Good good = list.get(index);
            if (client.deleteGood(good.getName())) {
                System.out.println("Successfully deleted a good!");
                System.out.print("Try deleting a good again?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            } else {
                System.out.println("Good was not edited =(");
                System.out.print("Try editing a good again?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            }
        }
    }

    private void editGoodDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            List<Good> list = client.getGoods(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no goods to edit!");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the good to edit: ");
            int index;
            String integer = scanner.nextLine();
            try {
                index = Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            Good good = list.get(index);
            System.out.print("Enter price of the good: ");
            double price;
            String real = scanner.nextLine();
            try {
                price = Double.parseDouble(real);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (price < 0) {
                System.out.println("Illegal price value!");
                continue;
            }
            System.out.print("Enter amount of the good: ");
            int amount;
            String amountStr = scanner.nextLine();
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (amount < 0) {
                System.out.println("Illegal amount value!");
                continue;
            }
            System.out.print("Enter description of the good: ");
            String description = scanner.nextLine();
            System.out.print("Enter producer of the good: ");
            String producer = scanner.nextLine();
            good.setPrice(price);
            good.setAmount(amount);
            good.setProducer(producer);
            good.setDescription(description);
            if (client.updateGood(good)) {
                System.out.println("Successfully edited a good!");
                thisMenu = false;
            } else {
                System.out.println("Good was not edited =(");
                System.out.print("Try editing a good again?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            }
        }
    }

    private void createGoodDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            System.out.print("Enter name of the good: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                System.out.println("Empty names are forbidden");
                continue;
            }
            List<GoodGroup> list = client.getGroups(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no groups to add good to!");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the group to add good to: ");
            int index;
            String integer = scanner.nextLine();
            try {
                index = Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            GoodGroup goodGroup = list.get(index);
            System.out.print("Enter price of the good: ");
            double price;
            String real = scanner.nextLine();
            try {
                price = Double.parseDouble(real);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (price < 0) {
                System.out.println("Illegal price value!");
                continue;
            }
            System.out.print("Enter amount of the good: ");
            int amount;
            String amountStr = scanner.nextLine();
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (amount < 0) {
                System.out.println("Illegal amount value!");
                continue;
            }
            System.out.print("Enter description of the good: ");
            String description = scanner.nextLine();
            System.out.print("Enter producer of the good: ");
            String producer = scanner.nextLine();
            Good good = new Good(name);
            good.setPrice(price);
            good.setAmount(amount);
            good.setProducer(producer);
            good.setDescription(description);
            if (client.addGood(goodGroup.getName(), good)) {
                System.out.println("Successfully added a good!");
                thisMenu = false;
            } else {
                System.out.println("Good was not added, the most likely reason is it's name is not unique!");
                System.out.print("Try adding a good again?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            }
        }
    }

    private void workWithGroups() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            System.out.print("""
                    To create a group press 1,
                    To edit a group press 2,
                    To delete a group press 3,
                    To display all groups press 4,
                    To display the price of all goods within a group press 5,
                    To go back press 6:                                       
                    """);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createGroupDialogue();
                case "2" -> editGroupDialogue();
                case "3" -> deleteGroupDialogue();
                case "4" -> displayGroupDialogue();
                case "5" -> displayPriceOfAGroupDialogue();
                case "6" -> thisMenu = false;
                default -> System.out.println("Illegal choice was maid");
            }
        }
    }

    private void displayPriceOfAGroupDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            List<GoodGroup> list = client.getGroups(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no groups to display full price of!");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the group to display full price of: ");
            int index;
            String name = scanner.nextLine();
            try {
                index = Integer.parseInt(name);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            GoodGroup goodGroup = list.get(index);
            List<Good> goods = client.getGoods(goodGroup.getName(), Integer.MAX_VALUE);
            double sum = 0;
            for (Good good : goods)
                sum += good.getPrice() * good.getAmount();
            System.out.println("The group is named: " + goodGroup.getName());
            System.out.println("The full price of the group is: " + sum);
            thisMenu = false;
        }
    }

    private void displayGroupDialogue() throws IOException, NoSuchAlgorithmException {
        List<GoodGroup> list = client.getGroups(Integer.MAX_VALUE);
        if (list.isEmpty()) {
            System.out.println("There are no groups to delete");
            return;
        }
        for (int i = 0; i < list.size(); i++)
            System.out.println("" + i + ". " + list.get(i));
    }

    private void deleteGroupDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            List<GoodGroup> list = client.getGroups(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no groups to delete");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the group to delete: ");
            int index;
            String name = scanner.nextLine();
            try {
                index = Integer.parseInt(name);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            GoodGroup goodGroup = list.get(index);
            if (client.deleteGroup(goodGroup.getName())) {
                System.out.println("Successfully deleted a group!");
                System.out.print("Try deleting a group again?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            } else {
                System.out.println("Deletion failed!");
            }
        }
    }

    private void editGroupDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            List<GoodGroup> list = client.getGroups(Integer.MAX_VALUE);
            if (list.isEmpty()) {
                System.out.println("There are no groups to edit");
                return;
            }
            for (int i = 0; i < list.size(); i++)
                System.out.println("" + i + ". " + list.get(i));
            System.out.print("Enter index of the group to edit: ");
            int index;
            String name = scanner.nextLine();
            try {
                index = Integer.parseInt(name);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number format!");
                continue;
            }
            if (index >= list.size() || index < 0) {
                System.out.println("Illegal index value!");
                continue;
            }
            System.out.print("Enter description of the group: ");
            String description = scanner.nextLine();
            GoodGroup goodGroup = list.get(index);
            goodGroup.setDescription(description);
            if (client.updateGroup(goodGroup)) {
                System.out.println("Successfully updated a group!");
                System.out.print("Try editing another group?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            } else {
                System.out.println("Update failed!");
            }
        }
    }

    private void createGroupDialogue() throws IOException, NoSuchAlgorithmException {
        boolean thisMenu = true;
        while (thisMenu) {
            System.out.print("Enter name of the group: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                System.out.println("Empty names are forbidden");
                continue;
            }
            System.out.print("Enter description of the group: ");
            String description = scanner.nextLine();
            GoodGroup goodGroup = new GoodGroup(name, description);
            if (client.addGroup(goodGroup)) {
                System.out.println("Successfully added a group!");
                thisMenu = false;
            } else {
                System.out.println("Group was not added, the most likely reason is it's name is not unique!");
                System.out.print("Try adding a group again?(Y/N): ");
                String yn = scanner.nextLine();
                if (yn.equals("N"))
                    thisMenu = false;
            }
        }
    }
}
