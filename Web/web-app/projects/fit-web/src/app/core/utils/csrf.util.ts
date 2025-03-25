const extractCsrfToken = (cookies: string, searchMame: string) => {
  const cookieArr = cookies.split('; ');
  let i: number = 0;
  for (let i = 0; i < cookieArr.length; i++) {
    const [name, value] = cookieArr[i].split('=');
    if (name === searchMame) {
      return value;
    }
  }
  return null;
}

export const csrfUtil: {
  extractCsrfToken: (cookies: string, name: string) => string | null
} = {
  extractCsrfToken
}
