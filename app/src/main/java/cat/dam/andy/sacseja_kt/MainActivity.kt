package cat.dam.andy.sacseja_kt

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {
    // Members
    private val IDLE_TIME = 200 //temps mínim entre sacsejades (ms)
    val SENSIBILITY = 2 // sensibilitat del sensor
    private var sensorManager: SensorManager? = null //gestor de sensors
    private var colorFlag = false //per canviar color
    private var lastUpdate: Long = 0 //per control de temps mínim entre sacsejades

    // Views
    private lateinit var tvInstructions: TextView //per instruccions i canvi de color

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initSensor()
    }

    private fun initViews() {
        tvInstructions = findViewById(R.id.tv_instructions)
        tvInstructions.setBackgroundColor(Color.RED)
    }

    private fun initSensor() {
        //obtenim el gestor de sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        //obtenim l'hora actual en milisegons per controlar un temps mínim entre sacsejades
        lastUpdate = System.currentTimeMillis()
    }

    //sobreescriu els mètodes del SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event)
        }
    }

    override fun onResume() {
        super.onResume()
        // Registra aquesta classe com a listener dels sensors d'accelereració
        sensorManager!!.registerListener(
            this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        // desregistra el listener quan passa a segon pla
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    //Si hi ha algun canvi en els sensors registrats, es cridarà aquest mètode
    private fun getAccelerometer(event: SensorEvent) {
        //obtenim els valors del sensor
        val values = event.values
        // Moviment en els eixos x, y i z
        val x = values[0]
        val y = values[1]
        val z = values[2]
        val normalizedAcceleration = ((x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH))
        val actualTime = System.currentTimeMillis()
        //Per testejar valors
        //Toast.makeText(getApplicationContext(),String.valueOf(accelationSquareRoot)+" "+ SensorManager.GRAVITY_EARTH,Toast.LENGTH_SHORT).show();
        if (normalizedAcceleration >= SENSIBILITY) //s'esecutarà si es sacseja un mínim
        {
            //Si fa poc temps dels darrer canvi no fem res
            if (actualTime - lastUpdate < IDLE_TIME) {
                return
            }
            //quan ha passat el mínim temps entre sacsejades
            lastUpdate = actualTime // actualitzem el temps
            //canviem el color de fons
            changeBackgroundColor(colorFlag)
            colorFlag = !colorFlag
        }
    }

    private fun changeBackgroundColor(isColor: Boolean) {
        if (isColor) {
            tvInstructions.setBackgroundColor(Color.GREEN)
        } else {
            tvInstructions.setBackgroundColor(Color.RED)
        }
    }
}