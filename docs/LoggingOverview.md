| Bestand                        | Log Statement                                               | Level  | Logbestand            | Moment                           |
|--------------------------------|-------------------------------------------------------------|--------|-----------------------|----------------------------------|
| **BaService.java**             | "Berekening gelukt: kenmerk={} opgeslagen-op={} totalen={}" | INFO   | ba-calc.log           | Na succesvol opslaan in database |
| **BaController.java** | "POST /api/v1/ba/{}/calculate-totals berekening ontvangen payload={{kenmerk={}, bijdragePercentage={}}}" | INFO | app.log | Begin van endpoint call  |
|                                | "POST /api/v1/ba/{}/calculate-totals response verstuurd in {}ms" | INFO   | app.log               | Bij verzenden response           |
| **BaValidator.java**           | "Validatie mislukt kenmerk={} fout={}"                      | WARN   | ba-calc.log           | Bij validatiefout                |
| **BaCalculator.java**          | "Berekening mislukt: kenmerk={} fout={}"                    | ERROR  | ba-calc.log           | Bij mislukte calculatie          |
| **GlobalExceptionHandler.java**| "ResponseStatusException: {} - {}"                          | ERROR  | app.log               | Bij ResponseStatusException      |     
|                                | "IllegalArgumentException: {}"                              | ERROR  | app.log               | Bij IllegalArgumentException     |
|                                | "DataAccessException: Database fout - {}"                   | ERROR  | app.log               | Bij database fout                |
|                                | "Onverwachte fout: error={}"                                | ERROR  | app.log               | Overige fouten                   |
| **LogsController.java**        | "Logs opgehaald voor kenmerk={} (last: {})"                 | INFO   | app.log               | Bij ophalen logs                 |
| **LogService.java**            | "Logbestand niet gevonden: Kenmerk={}"                      | WARN   | app.log               | Bij ontbreken logbestand         |







