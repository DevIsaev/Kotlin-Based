package com.example.buetoothbase

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buetoothbase.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var binding: ActivityMainBinding
        lateinit var bthAdapter: BluetoothAdapter
        lateinit var bthManager: BluetoothManager
        private var REQUEST_CODE_ENABLE_BT: Int = 1
        private var REQUEST_CODE_DISCOVERABLE_BT: Int = 2

        private lateinit var devicesStatusMap: MutableMap<String, String>
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        devicesStatusMap = mutableMapOf()
        bthManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bthAdapter = bthManager.adapter

        // Регистрация BroadcastReceiver для отслеживания подключений/отключений устройств
        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(bluetoothReceiver, filter)

        // Проверка доступности Bluetooth
        if (bthAdapter == null) {
            binding.status.text = "БЛЮТУЗ НЕДОСТУПЕН"
        } else {
            binding.status.text = "БЛЮТУЗ ДОСТУПЕН"
        }

        // Обновление изображения статуса Bluetooth
        updateBluetoothIcon()

        // Инициализация статусов подключенных устройств
        if (bthAdapter.isEnabled) {
            checkConnectedDevices() // Проверяем, какие устройства подключены на данный момент
        }

        // Обработчик для кнопки включения Bluetooth
        binding.buttonON.setOnClickListener {
            if (bthAdapter.isEnabled) {
                Toast.makeText(this, "БЛЮТУЗ УЖЕ ВКЛЮЧЕН", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }

        // Обработчик для кнопки выключения Bluetooth
        binding.buttonOFF.setOnClickListener {
            if (!bthAdapter.isEnabled) {
                Toast.makeText(this, "БЛЮТУЗ УЖЕ ВЫКЛЮЧЕН", Toast.LENGTH_SHORT).show()
            } else {
                bthAdapter.disable()
                binding.imageView.setImageResource(R.drawable.baseline_bluetooth_disabled_24)
                Toast.makeText(this, "БЛЮТУЗ ВЫКЛЮЧЕН", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработчик для кнопки поиска устройств
        binding.buttonDiscover.setOnClickListener {
            if (!bthAdapter.isDiscovering) {
                Toast.makeText(this, "ПОИСК УСТРОЙСТВА", Toast.LENGTH_SHORT).show()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }

        // Кнопка для отображения списка устройств
        binding.buttonDEVICES.setOnClickListener {
            if (bthAdapter.isEnabled) {
                updateDeviceStatusUI()
            } else {
                Toast.makeText(this, "БЛЮТУЗ НЕ ВКЛЮЧЕН", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkConnectedDevices() {
        // Используем BluetoothProfile для проверки активных подключений
        bthAdapter.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.HEADSET || profile == BluetoothProfile.A2DP || profile == BluetoothProfile.HEALTH) {
                    val connectedDevices = proxy.connectedDevices // Получаем список подключенных устройств
                    for (device in connectedDevices) {
                        devicesStatusMap[device.address] = "Подключено"
                    }
                }
                bthAdapter.closeProfileProxy(profile, proxy) // Освобождаем ресурсы
                updateDeviceStatusUI()
            }

            override fun onServiceDisconnected(profile: Int) {
                // Действия при отключении профиля (если нужно)
            }
        }, BluetoothProfile.HEADSET)
    }

    private fun updateBluetoothIcon() {
        if (bthAdapter.isEnabled) {
            binding.imageView.setImageResource(R.drawable.baseline_bluetooth_24)
        } else {
            binding.imageView.setImageResource(R.drawable.baseline_bluetooth_disabled_24)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT -> if (resultCode == Activity.RESULT_OK) {
                binding.imageView.setImageResource(R.drawable.baseline_bluetooth_24)
                Toast.makeText(this, "БЛЮТУЗ ВКЛЮЧЕН", Toast.LENGTH_SHORT).show()
                checkConnectedDevices() // Проверяем подключенные устройства после включения Bluetooth
            } else {
                Toast.makeText(this, "НЕ УДАЛОСЬ ПОДКЛЮЧИТЬ", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getDeviceType(deviceClass: Int): String {
        return when (deviceClass) {
            BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> "Наушники"
            BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> "Гарнитура"
            BluetoothClass.Device.PHONE_SMART -> "Смартфон"
            BluetoothClass.Device.PHONE_CELLULAR -> "Мобильный телефон"
            BluetoothClass.Device.WEARABLE_WRIST_WATCH -> "Часы"
            BluetoothClass.Device.COMPUTER_LAPTOP -> "Ноутбук"
            BluetoothClass.Device.COMPUTER_DESKTOP -> "Настольный компьютер"
            BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> "Автомобильная аудиосистема"
            BluetoothClass.Device.HEALTH_THERMOMETER -> "Термометр"
            else -> "Неизвестное устройство"
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        devicesStatusMap[it.address] = "Подключено"
                        updateDeviceStatusUI()
                    }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        devicesStatusMap[it.address] = "Не подключено"
                        updateDeviceStatusUI()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateDeviceStatusUI() {
        binding.DEVICES.visibility = View.VISIBLE
        binding.DEVICES.text = "Подключенные устройства"
        val devices = bthAdapter.bondedDevices
        for (device in devices) {
            val dName = device.name
            val dAddress = device.address
            val dClass = device.bluetoothClass
            val deviceType = getDeviceType(dClass.deviceClass)
            val deviceStatus = devicesStatusMap[dAddress] ?: "Не подключено"
            binding.DEVICES.append("\nУстройство: $dName, Тип: $deviceType, Адрес: $dAddress, Статус: $deviceStatus")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
    }
}

