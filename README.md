# Minecraft Monitor — Agent

> Wtyczka Paper zbierająca metryki środowiska serwera gry w czasie rzeczywistym.  
> Część systemu **System monitorowania środowiska serwera gier** - projekt inżynierski.

---

> ⚠️ **Plugin jest aktualnie w trakcie rozwoju.** Poniższa dokumentacja będzie rozbudowywana wraz z postępem prac.

## Wymagania

- Java 21+
- Paper 1.21+

## Konfiguracja

Plik `plugins/MinecraftMonitor/config.yml` - generowany automatycznie przy pierwszym uruchomieniu:

```yaml
backend-url: http://localhost:8080   # adres backendu Minecraft Monitor
api-key: ''                          # generowany automatycznie
server-uuid: ''                      # generowany automatycznie
interval-seconds: 10                 # interwał zbierania metryk (sekundy)
```

Plik `plugins/MinecraftMonitor/messages.yml`:

```yaml
no-permission: "&cNie masz uprawnień do tej komendy."
usage: "&eUżycie: /monitor <reload|resetkey>"
unknown-subcommand: "&cNieznana subkomenda. Dostępne: reload, resetkey"
config-reloaded: "&aKonfiguracja monitora została przeładowana."
key-reset: "&aKlucz API został zresetowany. Nowy klucz: &f{key}"
```

## Komendy

| Komenda | Opis | Uprawnienie |
|---|---|---|
| `/monitor reload` | Przeładowuje konfigurację bez restartu serwera | `monitor.admin` |
| `/monitor resetkey` | Generuje nowy API key i zapisuje do config.yml | `monitor.admin` |

## Licencja

MIT
