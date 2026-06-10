package com.example.ui.theme

enum class AppLanguage(val code: String, val label: String) {
    ES("es", "Español (Ar)"),
    EN("en", "English")
}

object T {
    private val translations = mapOf(
        AppLanguage.ES to mapOf(
            "app_name" to "Peso Wallet",
            "tagline" to "La súper-app de stablecoins para Argentina",
            "get_started" to "Empezar ya",
            "next" to "Siguiente",
            "skip" to "Omitir",
            "back" to "Volver",
            "confirm" to "Confirmar",
            "cancel" to "Cancelar",
            "save" to "Guardar",
            "success" to "Éxito",
            "error" to "Error",
            "insufficient" to "Saldo insuficiente",
            "network" to "Red",
            "amount" to "Monto",
            "recipient" to "Destinatario",
            "note" to "Nota / Referencia",
            "fee" to "Comisión",
            "completed" to "Completado",
            "pending" to "Pendiente",
            "failed" to "Fallido",

            // Onboarding
            "onboarding_title_1" to "Dólares Digitales",
            "onboarding_desc_1" to "Ahorrá en dólares digitales (USDT, UXD, USDC) y protegé tus fondos de la devaluación de manera simple.",
            "onboarding_title_2" to "Pagos Instantáneos QR",
            "onboarding_desc_2" to "Pagá en comercios o transferí a amigos al instante escaneando cualquier código QR compatible.",
            "onboarding_title_3" to "Intercambio Sin Fricción",
            "onboarding_desc_3" to "Convertí entre pesos y dólares digitales en un solo clic con las mejores cotizaciones del mercado.",

            // Auth
            "auth_title" to "Ingresá tu PIN",
            "auth_subtitle" to "Acceso seguro a Peso Wallet",
            "auth_biometrics" to "Usar huella / rostro",
            "auth_forgot" to "¿Olvidaste tu PIN?",
            "auth_error" to "PIN incorrecto. Reingresar.",

            // Dashboard
            "total_balance" to "Saldo Total Valorado",
            "send" to "Enviar",
            "receive" to "Recibir",
            "scan" to "Escanear QR",
            "vault" to "Bóveda / Ahorro",
            "assets_title" to "Mis Activos",
            "recent_tx" to "Transacciones Recientes",
            "view_all" to "Ver todo",
            "today_change" to "Cambio de hoy",
            
            // Send
            "send_stable" to "Enviar Fondos",
            "enter_address" to "Dirección, CVU/CBU o Alias",
            "select_asset" to "Seleccionar Activo",
            "review_tx" to "Revisar Transacción",
            "sending_to" to "Enviando a",
            "will_receive" to "Recibirá",
            "slide_to_send" to "Deslizar para confirmar pago",
            "tx_sent" to "Transacción Enviada",
            "tx_receipt" to "Comprobante de Pago",
            
            // Receive
            "receive_stable" to "Recibir Pagos",
            "your_address" to "Tu dirección de cobro",
            "copy" to "Copiar dirección",
            "copied" to "¡Copiado al portapapeles!",
            "request_amount" to "Fijar monto de cobro (QR Dinámico)",
            "generate_qr" to "Generar Código QR",
            
            // QR
            "qr_scanner" to "Escanear QR de Pago",
            "scan_merchant_desc" to "Escaneá QR de Transferencias, Interbanking o stablecoins",
            "merchant_name" to "Comercio",
            "payment_success" to "¡Pago Realizado con Éxito!",
            "tap_to_sim" to "Toca para simular escaneo",
            
            // Savings
            "savings_vault" to "Bóveda de Ahorros",
            "apy_stable" to "Rendimiento Anual (APY)",
            "savings_desc" to "Bóveda descentralizada de stablecoins autorrendimiento. Los intereses se acreditan cada segundo.",
            "vault_balance" to "Saldo en Bóveda",
            "accruing_interest" to "Ganado históricamente",
            "withdraw" to "Retirar",
            "deposit" to "Depositar",
            "vault_deposit_title" to "Depositar en Bóveda",
            "vault_withdraw_title" to "Retirar de Bóveda",
            
            // Merchant Setup
            "merchant_mode" to "Modo Comercio",
            "merchant_dashboard" to "Panel del Comercio",
            "setup_business" to "Configurar Comercio",
            "business_name" to "Nombre de la Empresa",
            "cuit" to "CUIT del Comercio",
            "rubro" to "Rubro / Categoría",
            "daily_revenue" to "Ingresos del día",
            "settlements" to "Dólares liquidados",
            "generate_invoice" to "Cobro Comercio (Facturar)",
            "invoice_created" to "Factura enviada",
            
            // Price Dashboard
            "price_dashboard" to "Monitoreo de Precios",
            "price_alerts" to "Alertas de Precio",
            "create_alert" to "Crear Alerta de Precio",
            "notify_when" to "Notificarme cuando",
            "goes_above" to "Suba de",
            "goes_below" to "Baje de",
            "active_alerts" to "Alertas Activas",

            // Security
            "security_center" to "Centro de Seguridad Center",
            "biometrics_active" to "Biometría Activa",
            "finger_face" to "Huella y Rostro configurados",
            "backup_wallet" to "Frase de Recuperación",
            "backup_desc" to "Respaldá tus 12 palabras clave fuera de línea.",
            "connected_devices" to "Dispositivos Conectados",
            "connected_devices_info" to "Dispositivo verificado y respaldado por hardware.",
            "audit_logs_label" to "Registro de Auditoría de Seguridad",

            // Settings
            "settings" to "Ajustes",
            "language" to "Idioma",
            "currency_display" to "Moneda de Visualización",
            "network_selection" to "Red Blockchain por Defecto",
            "theme" to "Tema Visual (Modo Oscuro)",
            "help_center" to "Soporte e Información",
            "dev_options" to "Opciones de Desarrollador",
            "dev_logs" to "Consola de Eventos en Vivo (Auditoría)",
            "dev_seed" to "Resetear Base de Datos",
            "dev_simulate" to "Simular Fluctuación de Mercado",
            "help_desc" to "Peso Wallet es una billetera sin custodia experimental, regulada bajo la normativa tributaria local en Argentina."
        ),
        AppLanguage.EN to mapOf(
            "app_name" to "Peso Wallet",
            "tagline" to "The premium stablecoin super-app for Argentina",
            "get_started" to "Get Started",
            "next" to "Next",
            "skip" to "Skip",
            "back" to "Back",
            "confirm" to "Confirm",
            "cancel" to "Cancel",
            "save" to "Save",
            "success" to "Success",
            "error" to "Error",
            "insufficient" to "Insufficient balance",
            "network" to "Network",
            "amount" to "Amount",
            "recipient" to "Recipient",
            "note" to "Note / Bill Details",
            "fee" to "Fee",
            "completed" to "Completed",
            "pending" to "Pending",
            "failed" to "Failed",

            // Onboarding
            "onboarding_title_1" to "Digital Dollars",
            "onboarding_desc_1" to "Store funds in dollar-pegged stablecoins (USDT, UXD, USDC) and easily hedge against peso inflation.",
            "onboarding_title_2" to "Instant QR Payments",
            "onboarding_desc_2" to "Pay merchants or transfer funds to peers instantly by scanning any compatible QR code.",
            "onboarding_title_3" to "Frictionless Swaps",
            "onboarding_desc_3" to "Convert between fiat Pesos and digital Dollars in one tap with the tightest rates.",

            // Auth
            "auth_title" to "Enter Secure PIN",
            "auth_subtitle" to "Access your safe funds inside Peso Wallet",
            "auth_biometrics" to "Use Fingerprint / Face ID",
            "auth_forgot" to "Forgot Security PIN?",
            "auth_error" to "Access denied: PIN incorrect. Try again.",

            // Dashboard
            "total_balance" to "Estimated Value Balance",
            "send" to "Send",
            "receive" to "Receive",
            "scan" to "Scan QR",
            "vault" to "Savings Vault",
            "assets_title" to "My Digital Portfolio",
            "recent_tx" to "Recent Transactions",
            "view_all" to "View All",
            "today_change" to "Today's drift",
            
            // Send
            "send_stable" to "Transfer Assets",
            "enter_address" to "Recipient Wallet, CBU/CVU or Alias",
            "select_asset" to "Select stable or fiat asset",
            "review_tx" to "Inspect Transaction",
            "sending_to" to "Transacting to",
            "will_receive" to "Amount received",
            "slide_to_send" to "Slide slider to authorize transfer",
            "tx_sent" to "Asset Transfer Dispatched",
            "tx_receipt" to "Official Wallet Voucher",
            
            // Receive
            "receive_stable" to "Collect Assets",
            "your_address" to "Your receiving address",
            "copy" to "Copy key address",
            "copied" to "Copied to clipboard!",
            "request_amount" to "Request target amount (Dynamic QR)",
            "generate_qr" to "Create Code QR",
            
            // QR
            "qr_scanner" to "Scan QR Payment",
            "scan_merchant_desc" to "Scan peer QR, business QR or standard bill invoices",
            "merchant_name" to "Merchant",
            "payment_success" to "Wire Transfer Completed Successfully!",
            "tap_to_sim" to "Click to emulate scan",
            
            // Savings
            "savings_vault" to "Stablecoin Vault",
            "apy_stable" to "Annual Percentage Yield (APY)",
            "savings_desc" to "Stablecoin yield generator. Earn compound interest updated recursively in real-time.",
            "vault_balance" to "Balance in Vault",
            "accruing_interest" to "Lifetime Earned Interest",
            "withdraw" to "Withdraw",
            "deposit" to "Deposit",
            "vault_deposit_title" to "Vault Safe Deposit",
            "vault_withdraw_title" to "Vault Trust Drawback",
            
            // Merchant Setup
            "merchant_mode" to "Merchant Core",
            "merchant_dashboard" to "Merchant Hub",
            "setup_business" to "Establish Store Profile",
            "business_name" to "Enterprise Registry Title",
            "cuit" to "Official Tax License (CUIT)",
            "rubro" to "Business Category",
            "daily_revenue" to "Today's Sales Revenue",
            "settlements" to "Stablecoin Liquidated",
            "generate_invoice" to "B2C Merchant Voucher Invoice",
            "invoice_created" to "Voucher generated",
            
            // Price Dashboard
            "price_dashboard" to "Live Market Board",
            "price_alerts" to "Smart Price Watch",
            "create_alert" to "Set New Signal Anchor",
            "notify_when" to "Send signal when price",
            "goes_above" to "Moves above",
            "goes_below" to "Moves below",
            "active_alerts" to "Active Trigger Signals",

            // Security
            "security_center" to "Security Command",
            "biometrics_active" to "Biometrics Configured",
            "finger_face" to "Local credential binding active",
            "backup_wallet" to "Wallet Seed Backup",
            "backup_desc" to "Write down your 12 seed keys outside internet reach.",
            "connected_devices" to "Keystore Signers",
            "connected_devices_info" to "Certified node signed on hardware enclave.",
            "audit_logs_label" to "System Shield Log Auditing",

            // Settings
            "settings" to "Settings",
            "language" to "Preferred Language",
            "currency_display" to "Local Currency Reference",
            "network_selection" to "Defensive Blockchain Gateway",
            "theme" to "Dark System Aura Theme",
            "help_center" to "System Support Center",
            "dev_options" to "Special sandbox tools",
            "dev_logs" to "Real-time Auditable Events Terminal",
            "dev_seed" to "Hard Database Restoration",
            "dev_simulate" to "Trigger market update",
            "help_desc" to "Peso Wallet operates as an offline-first sovereign digital non-custodial gateway for Argentine retail savers."
        )
    )

    fun translate(language: AppLanguage, key: String): String {
        return translations[language]?.get(key) ?: key
    }
}
