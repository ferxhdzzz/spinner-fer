package bryan.miranda.ejemplospinner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassDoctores
import java.util.UUID

class pacientes : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_pacientes, container, false)
        val btnGuardarpacientes = root.findViewById<Button>(R.id.btnGuardarPaciente)
        val txtNombrepaciente = root.findViewById<EditText>(R.id.txtNombrePaciente)
        val txtEspecialidaddoc = root.findViewById<Spinner>(R.id.spDoctores)
        val txtfecha = root.findViewById<EditText>(R.id.txtFechaNacimiento)
        val txtdireccionpaciente = root.findViewById<EditText>(R.id.txtDireccionPaciente)

        btnGuardarpacientes.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //Guardar datos
                //1- Creo un objeto de la clase conexion
                val claseC = ClaseConexion().cadenaConexion()

                //2- creo una variable que contenga un PrepareStatement
                val addPaciente =
                    claseC?.prepareStatement("insert into tbPacientes(PacienteUUID, nombreDoctorUUID, Nombre, FechaNacimiento, Direccion) values(?, ?, ?, ?)")!!
                addPaciente.setString(1, UUID.randomUUID().toString())
                addPaciente.setString(2, txtNombrepaciente.text.toString())
                addPaciente.setString(3, txtEspecialidaddoc.toString())
                addPaciente.setString(4, txtfecha.text.toString())
                addPaciente.setString(5, txtdireccionpaciente.text.toString())

                addPaciente.executeUpdate()

                //Abro una corrutina para mostrar una alerta y limpiar los campos
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                    txtNombrepaciente.setText("")
                    txtfecha.setText("")
                    txtdireccionpaciente.setText("")

                }
            }

            val spdoctores = root.findViewById<Spinner>(R.id.spDoctores)

            //funcion que haga un select a los datos
            fun obtenerdoctores(): List<dataClassDoctores> {
                val objconexion = ClaseConexion().cadenaConexion()
                val statement = objconexion?.createStatement()
                val resultset = statement?.executeQuery("select * from tbDoctores")!!
                val listadoctores = mutableListOf<dataClassDoctores>()

                while (resultset.next()) {
                    val uuid = resultset.getString("DoctorUUID")
                    val nombre = resultset.getString("nombreDoctor")
                    val especialidad = resultset.getString("especialidad")
                    val telefono = resultset.getString("telefono")
                    val doccompleto = dataClassDoctores(uuid, nombre, especialidad, telefono)
                    listadoctores.add(doccompleto)
                }

                return listadoctores
            }
            //programar spinner
            CoroutineScope(Dispatchers.IO).launch {
                //datos que quiero mostrar
                val listadodedocs = obtenerdoctores()
                val nombredocs = listadodedocs.map { it.nombreDoctor }
                //configurar el adapter
                withContext(Dispatchers.Main) {
                    val miadaptador = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        nombredocs
                    )
                    spdoctores.adapter = miadaptador
                }
            }
        }
        return root
    }
}