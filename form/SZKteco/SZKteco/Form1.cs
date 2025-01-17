using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SZKteco
{
    public partial class Form1 : Form
    {
        public static Form1 INSTANCE;
        const int MaxCharacters = 5000;
        public void CambiarLabel(string texto)
        {
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
                notifyIcon1.Text = "Calistenia Bolivia";
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
            if (Service.isRun)
            {
                e.Cancel = true;
                this.Hide();
            }
            
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
