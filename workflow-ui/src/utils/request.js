// 封装 fetch 请求
const request = async (config) => {
  const { url, method = 'get', data } = config
  
  try {
    const options = {
      method,
      headers: {
        'Content-Type': 'application/json'
      }
    }
    
    if (data) {
      options.body = JSON.stringify(data)
    }
    
    const response = await fetch(url, options)
    
    if (!response.ok) {
      throw new Error('请求失败')
    }
    
    return await response.json()
  } catch (error) {
    console.error('请求错误:', error)
    throw error
  }
}

export default request 