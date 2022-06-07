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

        public Form1()
        {
            InitializeComponent();
            Service _service = new Service();
            _service.start();
        }

        protected override void OnClosed(EventArgs e)
        {
            Service.isRun = false;
            Service.t1.Abort();
            base.OnClosed(e);
        }
    }
}
