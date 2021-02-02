package com.palmeiro;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String URL_BASE = "https://www.bilbaokirolak.com/";
    private static final String URL_LOGIN = URL_BASE + "bkonline2/doLogin.jsp";
    private static final String URL_RESERVATION = URL_BASE + "bkonline2/reservas/reservar_horas.jsp?codCom=11&codAct=26&numAut=&fechaReserva=05/02/2021&bkts=1612303550121";
    private static final String BKTS_PAOLA = URL_BASE + "1612303550121";
     bkts
    public static void main(String[] args) {


            System.setProperty("webdriver.chrome.driver", "/home/leonardo/Downloads/chromedriver_linux64/chromedriver");

//Initiating your chromedriver
        WebDriver driver=new ChromeDriver();

//Applied wait time
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//maximize window
        driver.manage().window().maximize();

//open browser with desried URL
        driver.get(URL_LOGIN);

        //fill information about user
        WebElement loginText = driver.findElement(By.name("codigo"));
        loginText.sendKeys("53291283F");

        WebElement passText = driver.findElement(By.name("clave"));
        passText.sendKeys("28032020");

        WebElement submitButton = driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[2]/div/button"));

        submitButton.click();

        //TODO verificar si es final de semana, se for final de semana reservar mais cedo

        driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom=11&codAct=26&numAut=&fechaReserva=05/02/2021&bkts=1612303550121");
        WebElement ulElement =
                driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/div[1]/ul"));
        List<WebElement> liTabgs = ulElement.findElements(By.tagName("li"));
        //List<WebElement> allElements = driver.findElements(By.xpath("/html/body/div[1]/div[15]/div[3]/div[1]/ul/li"));
        for (int i = 0; i < liTabgs.size(); i++)
        {
            List<WebElement> anchors = liTabgs.get(i).findElements(By.tagName("a"));
            for (WebElement element : anchors) {
                if(!element.getAttribute("href").contains("#")) {
                    System.out.println("Encontrou" + element.getText());
                    //split
                    String[] hourText = liTabgs.get(i).getText().split("-");
                    if(hourText[0].trim().equals("18:00")){
                        //TODO copiar o text do link e llamar el reservar pago com as informacioes de usuario.
                        System.out.println("has hour!!");
                    }
                }
            }
            System.out.println(liTabgs.get(i).getText());
        }
        driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_pago.jsp?codCom=11&codAct=26&numAut=&fechaReserva=05/02/2021&horaReserva=00011A00005;0;0;18:00&textoHora=18:00-18:59&numSuplementos=1&bkts=1612298148880");
        WebElement reservationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div/div/a[2]"));
        reservationButton.click();
        WebElement saveReservationButton = driver.findElement(By.xpath(" /html/body/div[1]/div[32]/div/div/div/a[2]"));
        saveReservationButton.click();

        driver.close();
    }
}
