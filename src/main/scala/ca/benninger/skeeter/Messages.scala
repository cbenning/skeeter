
package ca.benninger.skeeter

case class NewURLList(urls:Map[String,Boolean])
case class NewProduct(madein:String,name:String,price:String,reviewURL:String,imgURL:String,url:String,desc:String)
case class URLGetRequest()
case class URLGetResponse(url:String)

