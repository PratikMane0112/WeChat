export const uploadToCloudinary = async (pics,fileType) => {
    
    if (pics && fileType) {

        console.log("pics",pics,fileType)
      
      const data = new FormData();
      data.append("file", pics);
      data.append("upload_preset", process.env.REACT_APP_CLOUDINARY_UPLOAD_PRESET || "wechat");
      data.append("cloud_name", process.env.REACT_APP_CLOUDINARY_CLOUD_NAME || "dcpesbd8q");
  
      const cloudName = process.env.REACT_APP_CLOUDINARY_CLOUD_NAME || "dcpesbd8q";
      const res = await 
      fetch(`https://api.cloudinary.com/v1_1/${cloudName}/${fileType}/upload`, {
        method: "post",
        body: data,
      })
        
        const fileData=await res.json();
        console.log("url : ", fileData.url);
        return fileData.url
  
    } else {
      console.log("error");
    }
  };