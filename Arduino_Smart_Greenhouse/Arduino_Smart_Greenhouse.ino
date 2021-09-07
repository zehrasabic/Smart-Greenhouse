#include <Servo.h>
int servoPin=3;
Servo Servo1;
#define OK 1
#define NOTOK 2
#define TIMEOUT 3
#define SERIALTIMEOUT 3000
#include <SoftwareSerial.h>
#define GSMGPRSA6 Serial1
#ifndef GSMGPRSA6
SoftwareSerial(10,11);
#define A6brzinaprijenosa 115200
#else
#define A6brzinaprijenosa 115200
#endif
byte h = 0;
byte a[6];
byte b[6];
// SENZORI VLAZNOSTI TLA
const int vlaznost1_pin = A0;
const int vlaznost2_pin = A1;
const int vlaznost3_pin = A2;
const int vlaznost4_pin = A3;
//ZELJENE VRIJEDNOSTI TEMP. I VLAZ. TLA
int zeljvlaz1 = 0;
int zeljvlaz2 = 0;
int zeljvlaz3 = 0;
int zeljvlaz4 = 0;
int zeljtemp = 0;
//ZELJENE VRIJEDNOSTI -> SMS
int tempp = 0;
int vlaz11 = 0;
int vlaz22 = 0;
int vlaz33 = 0;
int vlaz44 = 0;
int SMSprimljen = 0;
//ZELJENE VRIJEDNOSTI -> GPRS
int idsada = 0;
int idpret = 0;
int tempStatus = 0;
int vlaz1Status = 0;
int vlaz2Status = 0;
int vlaz3Status = 0;
int vlaz4Status = 0;
int spajanjeError = 0;char end_c[2];
int nacinrada = 0;
//STVARNE VRIJEDNOSTI
int vlaznost1_mj, vlaznost2_mj, vlaznost3_mj, vlaznost4_mj;
byte vlaznost1, vlaznost2, vlaznost3, vlaznost4;
// SENZOR TEMPERATURE DS180B
#include <OneWire.h>
#include <DallasTemperature.h>
// Data wire -> pin 2
#define ONE_WIRE_BUS 27
OneWire oneWire(ONE_WIRE_BUS);
int temperatura = 0;
DallasTemperature sensors(&oneWire);
// GAS SENZOR (analogni pin)
int gasSenzor = A8;
byte A = 0;
// SVJETLOSNI SENZOR (digitalni pin)
int svjetloSenzor = A13;
unsigned int Svjetlo = 0;
// SENZORI NIVOA VODE
const int gornjiSenzor = A4;
const int donjiSenzor = A5;
int donji, gornji;
// PWM RASVJETA RGB
int pin1 = 9;
int pin2 = 10;
int pin3 = 11;
// --- RELEJI ---
int relej1 = 4; //ventil1
int relej2 = 5; //ventil2
int relej3 = 6; //ventil3
int relej4 = 7; //ventil4
//int relej5=33;
int relej6 = 35; //ventilator
int relej7 = 39; //pumpa
int relej8 = 41; //grijac
void setup() {
Serial.begin(115200);
while (!Serial) {
;
}
GSMGPRSA6.begin(A6brzinaprijenosa);
end_c[0] = 0x1a;
end_c[1] = '\0';
Serial3.begin(9600);
sensors.begin();
Servo1.attach(servoPin);pinMode(gasSenzor, INPUT);
pinMode(svjetloSenzor, INPUT);
pinMode(pin1, OUTPUT);
pinMode(pin2, OUTPUT);
pinMode(pin3, OUTPUT);
digitalWrite(pin1, LOW);
digitalWrite(pin2, LOW);
digitalWrite(pin3, LOW);
pinMode(relej1, OUTPUT);
pinMode(relej2, OUTPUT);
pinMode(relej3, OUTPUT);
pinMode(relej4, OUTPUT);
//pinMode(relej5, OUTPUT);
pinMode(relej6, OUTPUT);
pinMode(relej7, OUTPUT);
pinMode(relej8, OUTPUT);
digitalWrite(relej1, HIGH);
digitalWrite(relej2, HIGH);
digitalWrite(relej3, HIGH);
digitalWrite(relej4, HIGH);
digitalWrite(relej6, HIGH);
digitalWrite(relej7, HIGH);
digitalWrite(relej8, HIGH);
Servo1.write(0);
delay(5000);
//konfiguracija GSM/GPRS A6 modula
Serial.println("Pocetak rada modula...");
if(konfiguracijaA6() != OK) {
Serial.println("Greska!!!");
while(1 == 1);
}
//konfiguracija GPRS
Serial.println("Konfiguracija GPRS usluge...");
konfiguracijaGPRS();
//konfiguracija SMS
Serial.println("Konfiguracija SMS usluge...");
konfiguracijaSMS();
}
void konfiguracijaGPRS() {
naredbaA6("AT+CGATT?", "OK", "yy", 20000, 2);
naredbaA6("AT+CGATT=1", "OK", "yy", 20000, 2);
//naredbaA6("AT+CIPMUX=0", "OK", "yy", 20000, 2);
naredbaA6("AT+CGDCONT=1,\"IP\",\"active.bhmobile.ba\"", "OK", "yy", 20000, 2);
}
void konfiguracijaSMS() {
naredbaA6("AT+CSDH=1", "OK", "yy", 10000, 2);
naredbaA6("AT+CMGF=1", "OK", "yy", 10000, 2);
naredbaA6("AT+CMGD=1,3", "OK", "yy", 10000, 2);
naredbaA6("AT+CPMS=\"SM\",\"SM\",\"SM\"", "OK", "yy", 10000, 2);
}
bool konfiguracijaA6() {
GSMGPRSA6.println("AT+CREG?");
byte odgovor = cekajodgovorA6("1,", "1,", 1500);
while (odgovor != OK) {GSMGPRSA6.println("AT+CREG?");
odgovor = cekajodgovorA6("1,", "1,", 1500);
}
if (naredbaA6("AT&F0", "OK", "yy", 5000, 2) == OK) {
if (naredbaA6("ATE0", "OK", "yy", 5000, 2) == OK) {
if (naredbaA6("AT+CMEE=2", "OK", "yy", 5000, 2) == OK) {
return OK;
}
else {
return NOTOK;
}
}
}
}
String citajA6(){
String odgovor = "";
if (GSMGPRSA6.available()) {
odgovor = GSMGPRSA6.readString();
}
return odgovor;
}
byte cekajodgovorA6 (String odgovor1, String odgovor2, int timeOut) {
unsigned long ulaz = millis();
int brojac = 0;
String odgovor = citajA6();
byte retVal = 199;
do {
odgovor = citajA6();
if (odgovor != "") {
Serial.print ((millis() - ulaz));
Serial.print (" ms ");
Serial.println(odgovor);
if(odgovor.indexOf("+CMGL") >0) {
if(odgovor.indexOf("T:") > 0) {
String tt = odgovor.substring(odgovor.indexOf("T:") + 2, odgovor.indexOf(",V1"));
String v11 = odgovor.substring(odgovor.indexOf("V1:") + 3, odgovor.indexOf(",V2"));
String v22 = odgovor.substring(odgovor.indexOf("V2:") + 3, odgovor.indexOf(",V3"));
String v33 = odgovor.substring(odgovor.indexOf("V3:") + 3, odgovor.indexOf(",V4"));
String v44 = odgovor.substring(odgovor.indexOf("V4:") + 3, odgovor.indexOf("!"));
zeljtemp = tt.toInt();
zeljvlaz1 = v11.toInt();
zeljvlaz2 = v22.toInt();
zeljvlaz3 = v33.toInt();
zeljvlaz4 = v44.toInt();
SMSprimljen = 1;
}
if(odgovor.indexOf("odabranSms") > 0) {
nacinrada = 1;
String poruka = (String)"Sms usluga,T:" + (int)temperatura + ",V1:" + (int)vlaznost1 + ",V2:" + (int)vlaznost2 + ",V3:" + (int)vlaznost3 + ",V4:" + (int)vlaznost4 + "!";
//slanje povratne informacije za odabir sms
naredbaA6("AT+CMGS = \"+38762952916\"", ">", "yy", 25000, 2);
GSMGPRSA6.println(poruka);Serial.println(poruka);
GSMGPRSA6.println(end_c);
GSMGPRSA6.println();
delay(3000);
}
else if (odgovor.indexOf("odabranGprs") > 0) {
nacinrada = 2;
String poruka = (String)"Gprs usluga,T:" + (int)temperatura + ",V1:" + (int)vlaznost1 + ",V2:" + (int)vlaznost2 + ",V3:" + (int)vlaznost3 + ",V4:" + (int)vlaznost4 + "!";
//slanje povratne informacije za odabir gprs
naredbaA6("AT+CMGS = \"+38762952916\"", ">", "yy", 25000, 2);
GSMGPRSA6.println(poruka);
Serial.println(poruka);
GSMGPRSA6.println(end_c);
GSMGPRSA6.println();
delay(3000);
}
}
if (odgovor.lastIndexOf("</feed>") > 0){
String id = odgovor.substring(odgovor.indexOf("<entry-id type=\"integer\">") + 25, odgovor.indexOf("</entry-id>"));
String temp = odgovor.substring(odgovor.indexOf("<field1>") + 8, odgovor.indexOf("</field1>"));
String vlaz1 = odgovor.substring(odgovor.indexOf("<field2>") + 8, odgovor.indexOf("</field2>"));
String vlaz2 = odgovor.substring(odgovor.indexOf("<field3>") + 8, odgovor.indexOf("</field3>"));
String vlaz3 = odgovor.substring(odgovor.indexOf("<field4>") + 8, odgovor.indexOf("</field4>"));
String vlaz4 = odgovor.substring(odgovor.indexOf("<field5>") + 8, odgovor.indexOf("</field5>"));
idsada = id.toInt();
zeljtemp = temp.toInt();
zeljvlaz1 = vlaz1.toInt();
zeljvlaz2 = vlaz2.toInt();
zeljvlaz3 = vlaz3.toInt();
zeljvlaz4 = vlaz4.toInt();
}
if (odgovor.indexOf("+IPSTATUS: IP INITIAL") > 0) {
spajanjeError = 0;
}
if (odgovor.indexOf("+IPSTATUS: CONNECT OK") > 0) {
spajanjeError = 1;
}
if (odgovor.indexOf("+IPSTATUS: IP CLOSE") > 0) {
spajanjeError = 0;
}
/* if (odgovor.indexOf("+IPSTATUS: IP GPRSACT") > 0) {
spajanjeError = 0;
}*/
}
}
while ((odgovor.indexOf(odgovor1) + odgovor.indexOf(odgovor2) == -2) && millis() - ulaz < timeOut);
if ((millis() - ulaz) >= timeOut) {
retVal = TIMEOUT;
}else {
if (odgovor.indexOf(odgovor1) + odgovor.indexOf(odgovor2) > -2) retVal = OK;
else retVal = NOTOK;
}
return retVal;
}
byte naredbaA6 (String naredba, String odgovor1, String odgovor2, int timeOut, int ponavljanje) {
byte vracenaVrijednost = NOTOK;
byte brojac = 0;
while (brojac < ponavljanje && vracenaVrijednost != OK) {
GSMGPRSA6.println(naredba);
Serial.print("Naredba -> #");
Serial.print(naredba);
Serial.println("#");
if (cekajodgovorA6(odgovor1,odgovor2,timeOut) == OK) {
vracenaVrijednost = OK;
}
else {
vracenaVrijednost = NOTOK;
}
brojac++;
}
return vracenaVrijednost;
}
void slanjeSMS (int t, int v1, int v2, int v3, int v4) {
String poruka = (String)"T:" + (int)t + ",V1:" + (int)v1 + ",V2:" + (int)v2 + ",V3:" + (int)v3 + ",V4:" + (int)v4 + "!";
Serial.println("STVARNE VRIJEDNOSTI TEMPERATURE I VLAZNOSTI TLA POSLANE U APLIKACIJU SMS PORUKOM: ");
Serial.print("Temperatura => ");
Serial.print((int)(t));
Serial.println(" Â°C");
Serial.print("Vlaznost tla 1 => ");
Serial.print((int)(v1));
Serial.println(" %");
Serial.print("Vlaznost tla 2 => ");
Serial.print((int)(v2));
Serial.println(" %");
Serial.print("Vlaznost tla 3 => ");
Serial.print((int)(v3));
Serial.println(" %");
Serial.print("Vlaznost tla 4 => ");
Serial.print((int)(v4));
Serial.println(" %");
naredbaA6("AT+CMGS = \"+38762952916\"", ">", "yy", 25000, 2);
GSMGPRSA6.println(poruka);
Serial.println(poruka);
GSMGPRSA6.println(end_c);
GSMGPRSA6.println();
delay(3000);
}
void prijemSMS (int t, int v1, int v2, int v3, int v4) {
naredbaA6("AT+CMGL=\"REC UNREAD\"", "OK", "yy", 25000, 2);
if (SMSprimljen == 1){Serial.println("ZELJENE VRIJEDNOSTI TEMPERATURE I VLAZNOSTI TLA POSLANE SA APLIKACIJE SMS PORUKOM: ");
Serial.print("Temperatura => ");
Serial.print((int)(zeljtemp));
Serial.println(" Â°C");
Serial.print("Vlaznost tla 1 => ");
Serial.print((int)(zeljvlaz1));
Serial.println(" %");
Serial.print("Vlaznost tla 2 => ");
Serial.print((int)(zeljvlaz2));
Serial.println(" %");
Serial.print("Vlaznost tla 3 => ");
Serial.print((int)(zeljvlaz3));
Serial.println(" %");
Serial.print("Vlaznost tla 4 => ");
Serial.print((int)(zeljvlaz4));
Serial.println(" %");
if (zeljvlaz1 > vlaznost1) {
digitalWrite(relej3, LOW);
Serial.println("Ventil 1 -> ON");
}
else {
digitalWrite(relej3, HIGH);
Serial.println("Ventil 1 -> OFF");
}
if (zeljvlaz2 > vlaznost2) {
digitalWrite(relej2, LOW);
Serial.println("Ventil 2 -> ON");
}
else {
digitalWrite(relej2, HIGH);
Serial.println("Ventil 2 -> OFF");
}
if (zeljvlaz3 > vlaznost3) {
digitalWrite(relej1, LOW);
Serial.println("Ventil 3 -> ON");
}
else {
digitalWrite(relej1, HIGH);
Serial.println("Ventil 3 -> OFF");
}
if (zeljvlaz4 > vlaznost4) {
digitalWrite(relej4, LOW);
Serial.println("Ventil 4 -> ON");
}
else {
digitalWrite(relej4, HIGH);
Serial.println("Ventil 4 -> OFF");
}
if (zeljtemp > temperatura + 1) {
digitalWrite(relej8, LOW);
Servo1.write(0);
Serial.println("Grijac -> ON");
delay(2000);
}
else {
digitalWrite(relej8, HIGH);
Serial.println("Grijac -> OFF");}
if (zeljtemp < temperatura - 1) {
digitalWrite(relej6, LOW);
Servo1.write(45);
Serial.println("Ventilator -> ON");
delay(2000);
}
else {
digitalWrite(relej6, HIGH);
Servo1.write(0);
Serial.println("Ventilator -> OFF");
}
Serial.println("SLANJE SMS PORUKE SA POVRATNOM INFORMACIJOM I STVARNIM VRIJEDNOSTIMA U APLIKACIJU: ");
String poruka = (String)"Poruka primljena,T:" + (int)t + ",V1:" + (int)v1 + ",V2:" + (int)v2 + ",V3:" + (int)v3 + ",V4:" + (int)v4 + "!";
naredbaA6("AT+CMGS = \"+38762952916\"", ">", "yy", 25000, 2);
GSMGPRSA6.println(poruka);
Serial.println(poruka);
GSMGPRSA6.println(end_c);
GSMGPRSA6.println();
SMSprimljen = 0;
delay(3000);
}
else {
naredbaA6("AT+CMGD=1,3", "OK", "yy", 10000, 2);
}
}
void PreuzmiPosaljiThingSpeak (String kanal, String kljucZaCitanje, String kljucZaPisanje, String adresa, int temp, int vlaz1, int vlaz2, int vlaz3,
int vlaz4) {
//uspostava TCP
naredbaA6("AT+CIPSTATUS", "OK", "yy", 10000, 2);
if (spajanjeError == 0) {
naredbaA6("AT+CIPSTART=\"TCP\",\"" + adresa + "\",80", "CONNECT OK", "yy", 25000, 2);
String citajPodatke = (String)"GET /channels/" + kanal + "/feeds/last.xml?api_key=" + kljucZaCitanje + "&header=false";
//preuzimanje zeljenih vrijednosti temperature i vlaznosti tla
String naredba = "AT+CIPSEND=";
naredba += String(citajPodatke.length());
naredbaA6(naredba, ">", "yy", 10000, 1);
GSMGPRSA6.print(citajPodatke);
Serial.println(citajPodatke);
naredbaA6(end_c, "OK", "yy", 30000, 1);
Serial.println("ZELJENE VRIJEDNOSTI TEMPERATURE I VLAZNOSTI TLA POSLANE SA APLIKACIJE NA THINGSPEAK KANAL: ");
Serial.print("ID trenut. => ");
Serial.println((int)(idsada));
Serial.print("ID preth. => ");
Serial.println((int)(idpret));
Serial.print("Temperatura => ");
Serial.print((int)(zeljtemp));
Serial.println(" Â°C");
Serial.print("Vlaznost tla 1 => ");
Serial.print((int)(zeljvlaz1));
Serial.println(" %");
Serial.print("Vlaznost tla 2 => ");Serial.print((int)(zeljvlaz2));
Serial.println(" %");
Serial.print("Vlaznost tla 3 => ");
Serial.print((int)(zeljvlaz3));
Serial.println(" %");
Serial.print("Vlaznost tla 4 => ");
Serial.print((int)(zeljvlaz4));
Serial.println(" %");
naredbaA6("AT+CIPCLOSE", "OK", "yy", 15000, 1);
if(idsada != idpret) {
if (zeljvlaz1 > vlaznost1) {
digitalWrite(relej3, LOW);
Serial.println("Ventil 1 -> ON");
}
else {
digitalWrite(relej3, HIGH);
Serial.println("Ventil 1 -> OFF");
}
if (zeljvlaz2 > vlaznost2) {
digitalWrite(relej2, LOW);
Serial.println("Ventil 2 -> ON");
}
else {
digitalWrite(relej2, HIGH);
Serial.println("Ventil 2 -> OFF");
}
if (zeljvlaz3 > vlaznost3) {
digitalWrite(relej1, LOW);
Serial.println("Ventil 3 -> ON");
}
else {
digitalWrite(relej1, HIGH);
Serial.println("Ventil 3 -> OFF");
}
if (zeljvlaz4 > vlaznost4) {
digitalWrite(relej4, LOW);
Serial.println("Ventil 4 -> ON");
}
else {
digitalWrite(relej4, HIGH);
Serial.println("Ventil 4 -> OFF");
}
if (zeljtemp > temperatura + 1) {
digitalWrite(relej8, LOW);
Servo1.write(0);
Serial.println("Grijac -> ON");
delay(2000);
}
else {
digitalWrite(relej8, HIGH);
Serial.println("Grijac -> OFF");
}
if (zeljtemp < temperatura - 1) {
digitalWrite(relej6, LOW);
Servo1.write(45);
Serial.println("Ventilator -> ON");
delay(2000);
}else {
digitalWrite(relej6, HIGH);
Servo1.write(0);
Serial.println("Ventilator -> OFF");
}
int povratnainformacija = 1;
naredbaA6("AT+CGACT=1,1", "OK", "yy", 10000, 2);
naredbaA6("AT+CIPSTART=\"TCP\",\"" + adresa + "\",80", "CONNECT OK", "yy", 25000, 2);
Serial.println("POVRATNA INFORMACIJA I STVARNE VRIJEDNOSTI TEMPERATURE I VLAZNOSTI TLA POSLANE NA THINGSPEAK KANAL: ");
Serial.print("Povratna informacija => ");
Serial.println((int)(povratnainformacija));
Serial.print("Temperatura => ");
Serial.print((int)(temp));
Serial.println(" Â°C");
Serial.print("Vlaznost tla 1 => ");
Serial.print((int)(vlaz1));
Serial.println(" %");
Serial.print("Vlaznost tla 2 => ");
Serial.print((int)(vlaz2));
Serial.println(" %");
Serial.print("Vlaznost tla 3 => ");
Serial.print((int)(vlaz3));
Serial.println(" %");
Serial.print("Vlaznost tla 4 => ");
Serial.print((int)(vlaz4));
Serial.println(" %");
String ucitajPodatke = (String)"GET /update?api_key=" +
kljucZaPisanje + "&field1=" + String(temp) + "&field2=" + String(vlaz1) + "&field3=" + String(vlaz2) + "&field4=" + String(vlaz3) + "&field5=" + String(vlaz4) + "&field6=" + String(povratnainformacija)+ "&header=false";
String naredba2 = "AT+CIPSEND="; 
naredba2 += String(ucitajPodatke.length());
naredbaA6(naredba2, ">", "yy", 10000, 1);
GSMGPRSA6.print(ucitajPodatke);
Serial.println(ucitajPodatke);
naredbaA6(end_c, "OK", "yy", 30000, 1);
idpret=idsada;
delay(10000);
naredbaA6("AT+CIPCLOSE", "OK", "yy", 15000, 1);
}
else if (idsada == idpret) {
if (zeljvlaz1 > vlaznost1) {
digitalWrite(relej3, LOW);
Serial.println("Ventil 1 -> ON");
}
else {
digitalWrite(relej3, HIGH);
Serial.println("Ventil 1 -> OFF");
}
if (zeljvlaz2 > vlaznost2) {
digitalWrite(relej2, LOW);
Serial.println("Ventil 2 -> ON");
}
else {
digitalWrite(relej2, HIGH);
Serial.println("Ventil 2 -> OFF");
}if (zeljvlaz3 > vlaznost3) {
digitalWrite(relej1, LOW);
Serial.println("Ventil 3 -> ON");
}
else {
digitalWrite(relej1, HIGH);
Serial.println("Ventil 3 -> OFF");
}
if (zeljvlaz4 > vlaznost4) {
digitalWrite(relej4, LOW);
Serial.println("Ventil 4 -> ON");
}
else {
digitalWrite(relej4, HIGH);
Serial.println("Ventil 4 -> OFF");
}
if (zeljtemp > temperatura + 1) {
digitalWrite(relej8, LOW);
Servo1.write(0);
Serial.println("Grijac -> ON");
delay(2000);
}
else {
digitalWrite(relej8, HIGH);
Serial.println("Grijac -> OFF");
}
if (zeljtemp < temperatura - 1) {
digitalWrite(relej6, LOW);
Servo1.write(45);
Serial.println("Ventilator -> ON");
delay(2000);
}
else {
digitalWrite(relej6, HIGH);
Servo1.write(0);
Serial.println("Ventilator -> OFF");
}
//naredbaA6("AT+CIPSTATUS", "OK", "yy", 10000, 2);
naredbaA6("AT+CGACT=1,1", "OK", "yy", 10000, 2);
naredbaA6("AT+CIPSTART=\"TCP\",\"" + adresa + "\",80", "CONNECT OK", "yy", 25000, 2);
int povratnainformacija = 0;
Serial.println("POVRATNA INFORMACIJA I STVARNE VRIJEDNOSTI TEMPERATURE I VLAZNOSTI TLA POSLANE NA THINGSPEAK KANAL: ");
Serial.print("Povratna informacija => ");
Serial.println((int)(povratnainformacija));
Serial.print("Temperatura => ");
Serial.print((int)(temp));
Serial.println(" Â°C");
Serial.print("Vlaznost tla 1 => ");
Serial.print((int)(vlaz1));
Serial.println(" %");
Serial.print("Vlaznost tla 2 => ");
Serial.print((int)(vlaz2));
Serial.println(" %");
Serial.print("Vlaznost tla 3 => ");
Serial.print((int)(vlaz3));
Serial.println(" %");
Serial.print("Vlaznost tla 4 => ");Serial.print((int)(vlaz4));
Serial.println(" %");
String ucitajPodatke = (String)"GET /update?api_key=" +
kljucZaPisanje + "&field1=" + String(temp) + "&field2=" + String(vlaz1) + "&field3=" + String(vlaz2) + "&field4=" + String(vlaz3) + "&field5=" +
String(vlaz4) + "&field6=" + String(povratnainformacija) +"&header=false";
String naredba2 = "AT+CIPSEND=";
naredba2 += String(ucitajPodatke.length());
naredbaA6(naredba2, ">", "yy", 10000, 1);
GSMGPRSA6.print(ucitajPodatke);
Serial.println(ucitajPodatke);
naredbaA6(end_c, "OK", "yy", 30000, 1);
naredbaA6("AT+CIPCLOSE", "OK", "yy", 15000, 1);
}
}
else {
naredbaA6("AT+CIPCLOSE", "OK", "yy", 15000, 1);
}
}
long vrijemeslanja = 17;
long pocetnovrijeme = 0;
long proslovrijeme = 0;
void loop () {
String kanal = "1075839";
String kljucZaCitanje = "B0GNOKJ6HP6RS2I2";
String kljucZaPisanje = "OID9HYMWSXN5CW1I";
String adresa ="api.thingspeak.com";
vlaznost1_mj = analogRead(vlaznost1_pin);
vlaznost1 = ( 100 - ( (vlaznost1_mj / 1023.00) * 100 ) );
vlaznost2_mj = analogRead(vlaznost2_pin);
vlaznost2 = ( 100 - ( (vlaznost2_mj / 1023.00) * 100 ) );
vlaznost3_mj = analogRead(vlaznost3_pin);
vlaznost3 = ( 100 - ( (vlaznost3_mj / 1023.00) * 100 ) );
vlaznost4_mj = analogRead(vlaznost4_pin);
vlaznost4 = ( 100 - ( (vlaznost4_mj / 1023.00) * 100 ) );
if (vlaznost1 > 99) {
vlaznost1 = 99;
}
if (vlaznost2 > 99) {
vlaznost2 = 99;
}
if (vlaznost3 > 99) {
vlaznost3 = 99;
}
if (vlaznost4 > 99) {
vlaznost4 = 99;
}
//stanje temperature u stakleniku/plasteniku
sensors.requestTemperatures();
temperatura = sensors.getTempCByIndex(0);
//stanje zraka u stakleniku/plasteniku
int Gas = analogRead(gasSenzor);
if (Gas > 250) {
digitalWrite(relej6, LOW);
Servo1.write(45);delay(2000);
}
//osvijetljenost u stakleniku/plasteniku
Svjetlo = analogRead(svjetloSenzor);
digitalWrite(pin1, HIGH );
digitalWrite(pin2, HIGH);
digitalWrite(pin3, HIGH);
delay(3000);
//nivo vode u rezervoaru
gornji = analogRead(gornjiSenzor);
donji = analogRead(donjiSenzor);
if (donji < 200) {
digitalWrite(relej7, LOW);
A = 0;
}
else if (gornji > 200 ) {
digitalWrite(relej7, HIGH);
A = 1;
}
else if (A == 0) {
digitalWrite(relej7, LOW);
}
else if (A == 1) {
digitalWrite(relej7, HIGH);
}
else {
digitalWrite(relej7, HIGH);
}
//provjera novih SMS poruka -> salje se sa izbornika
naredbaA6("AT+CMGL=\"REC UNREAD\"", "OK", "yy", 25000, 2);
if (nacinrada == 1) {
vrijemeslanja = 1*80*1000UL;
//odabrana sms usluga
prijemSMS(temperatura, vlaznost1, vlaznost2, vlaznost3, vlaznost4);
proslovrijeme = millis() - pocetnovrijeme;
if (proslovrijeme > vrijemeslanja) {
slanjeSMS(temperatura, vlaznost1, vlaznost2, vlaznost3, vlaznost4);
pocetnovrijeme = millis();
}
}
else if(nacinrada == 2){
//odabrana gprs usluga
naredbaA6("AT+CGACT=1,1", "OK", "yy", 10000, 2);
PreuzmiPosaljiThingSpeak(kanal, kljucZaCitanje, kljucZaPisanje, adresa,
temperatura, vlaznost1, vlaznost2, vlaznost3, vlaznost4);
}
Serial3.flush();
h = h + 1;
if (h == 5) {
while (Serial3.available())
Serial3.read();
h = 0;
}
}
