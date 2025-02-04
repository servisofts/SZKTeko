using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;

namespace SZKteco
{
    public partial class Form1 : Form
    {
        public static Form1 INSTANCE;
        const int MaxCharacters = 5000;

        public void CambiarLabel(string texto, string colorText)
        {
            if (InvokeRequired)
            {
                this.Invoke((MethodInvoker)(() => CambiarLabel(texto, colorText)));
                return;
            }

            // Convierte el color proporcionado a un objeto de tipo Color
            Color color;
            try
            {
                color = Color.FromName(colorText);
            }
            catch
            {
                color = Color.Black; // Color predeterminado si el nombre es inválido
            }

            // Si el texto actual más el nuevo supera el límite
            if (textBox1.TextLength + texto.Length > MaxCharacters)
            {
                int extraCharacters = (textBox1.TextLength + texto.Length) - MaxCharacters;
                textBox1.Text = textBox1.Text.Substring(extraCharacters); // Recorta los caracteres antiguos
            }

            // Establece el color para el texto nuevo
            textBox1.SelectionStart = textBox1.TextLength;
            textBox1.SelectionLength = 0; // Sin seleccionar texto previo
            textBox1.ForeColor = Color.FromName(colorText);

            // Agrega el texto y el salto de línea
            textBox1.AppendText(texto + "\r\n");

            // Restablece el color predeterminado para futuros textos
            // textBox1.ForeColor = Color.White;
            // textBox1.ForeColor = System.Drawing.ColorTranslator.FromHtml("blue");
            textBox1.ForeColor = System.Drawing.ColorTranslator.FromHtml("#00a67d");
            // textBox1.ForeColor = System.Drawing.ColorTranslator.FromHtml("#00cc00");

            // Asegura que el caret esté visible al final
            textBox1.SelectionStart = textBox1.TextLength;
            textBox1.ScrollToCaret();
        }


        public void CambiarLabeliii(string texto, string colorText)
        {

           // this.textBox1.ForeColor = Color.FromName(colorText);
          //  this.textBox1.ForeColor = Color.Pink;

            if (InvokeRequired)
                this.Invoke((MethodInvoker)(() => {
                    //textBox1.Focus();
                    // Si el texto actual más el nuevo supera el límite
                    if (textBox1.TextLength + texto.Length > MaxCharacters)
                    {
                        // Calcula cuántos caracteres antiguos eliminar
                        int extraCharacters = (textBox1.TextLength + texto.Length) - MaxCharacters;

                        // Recorta los caracteres más antiguos
                        textBox1.Text = textBox1.Text.Substring(extraCharacters);
                    }

                    // Agrega el nuevo texto y ajusta la vista
                    textBox1.Text += texto + "\r\n";
                    textBox1.SelectionStart = textBox1.TextLength;
                    textBox1.ScrollToCaret();
                }
                ));
            else
                textBox1.Text += texto;
        }
        public Form1()
        {
            InitializeComponent();
            INSTANCE = this;
            Service _service = new Service();
            _service.start();
        }

        private void salirToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Service.isRun = false;
            Service.t1.Abort();
            Application.Exit();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.BeginInvoke((MethodInvoker)delegate {
                notifyIcon1.Text = "Calistenia Bolivia SRL";
                notifyIcon1.Visible = true;
                textBox1.Height = this.Height -28;
                textBox1.Width = this.Width-20;

                //this.Hide();

            });
        }


        private void verToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Show();
        }


        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {

 Service.isRun = false;
            Service.t1.Abort();
            Application.Exit();

            /*
            if (Service.isRun)
            {
                e.Cancel = true;
                this.Hide();
            }
            */

        }

        private void Form1_Resize(object sender, EventArgs e)
        {
            textBox1.Height = this.Height - 28;
            textBox1.Width = this.Width - 20;
        }

        private void textBox1_VisibleChanged(object sender, EventArgs e)
        {
            if (textBox1.Visible)
            {
                // textBox1.SelectionStart = textBox1.TextLength;
                //textBox1.ScrollToCaret();
                //textBox1.SelectionStart = textBox1.TextLength;
                //textBox1.Focus();
            }
        }
    }
}
