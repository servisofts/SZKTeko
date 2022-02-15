using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace capp
{
    internal class SJSon
    {
        private JObject jObject;

        public SJSon() { 
            jObject = new JObject();
        }
        public SJSon(String strJson)
        {
            strJson = strJson.Replace("\0", string.Empty);
            jObject = JObject.Parse(strJson);
        }
        public JObject tojObject() {
            return this.jObject;
        }
        public void put(string key, string value) {
            value = value.Replace("\0", string.Empty);
            if (!jObject.ContainsKey(key))
            {
                jObject.Add(key, value);
            }
            else {
                jObject.SelectToken(key).Replace(value);
            }
        }
        public void put(string key, int value)
        {
            if (!jObject.ContainsKey(key))
            {
                jObject.Add(key, value);
            }
            else
            {
                jObject.SelectToken(key).Replace(value);
            }
        }
        public void put(string key, bool value)
        {
            if (!jObject.ContainsKey(key))
            {
                jObject.Add(key, value);
            }
            else
            {
                jObject.SelectToken(key).Replace(value);
            }
        }
        public void put(string key, double value)
        {
            if (!jObject.ContainsKey(key))
            {
                jObject.Add(key, value);
            }
            else
            {
                jObject.SelectToken(key).Replace(value);
            }
        }
        public void put(string key,JArray arr)
        {
            if (!jObject.ContainsKey(key))
            {
                jObject.Add(key, arr);
            }
            else
            {
                jObject.SelectToken(key).Replace(arr);
            }
        }
        public void put(string key, SJSon value)
        {
            if (!jObject.ContainsKey(key))
            {
                jObject.Add(key, JObject.Parse(value.ToString()));
            }
            else
            {
                jObject.SelectToken(key).Replace(JObject.Parse(value.ToString()));
            }
        }
        public SJSon getSJSonObject(string key)
        {
            return new SJSon(jObject[key].ToString());
        }
        public string getString(string key) {
            if (jObject.ContainsKey(key))
            {
                return jObject[key].ToString();
            }
            return "";
        }

        public int getInt(string key)
        {
            if (jObject.ContainsKey(key))
            {
                return int.Parse(jObject[key].ToString());
            }
            return 0;
        }

        public double getDouble(string key)
        {
            if (jObject.ContainsKey(key))
            {
                return double.Parse(jObject[key].ToString());
            }
            return 0;
        }

        public bool getBool(string key)
        {
            if (jObject.ContainsKey(key))
            {
                return bool.Parse(jObject[key].ToString());
            }
            return false;
        }


        public bool has(string key) {
            return jObject.ContainsKey(key);
        }

        public string[] keys()
        {
            string[] keys = new string[jObject.Count];
            int i = 0;
            foreach (KeyValuePair<string, JToken> keyValuePair in jObject)
            {
                 keys[i] = keyValuePair.Key;
                 i++;
            }
            return keys;
        }
        public override String ToString()
        {
            return jObject.ToString(Newtonsoft.Json.Formatting.None);
        }
    }
}
