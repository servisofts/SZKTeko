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

        public void CambiarLabel(string texto)
        {
            if (InvokeRequired)
                this.Invoke((MethodInvoker)(() => textBox1.Text += texto));
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
                this.Hide();

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
            textBox1.Height = this.Height;
            textBox1.Width = this.Width;
        }
    }
}
