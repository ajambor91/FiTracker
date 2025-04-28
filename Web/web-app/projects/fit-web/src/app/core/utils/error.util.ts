const parseError = (error: any,) => {
  if (!error || !error.error.statusCode) {
    return "Unknown error. Please try again."
  }
  const errorStatus: number = error.error.statusCode;
  if (errorStatus === 401) {
    return "Unauthorized."
  } else if (errorStatus === 404) {
    return "Cannot find the resource you requested."

  } else {
    return "Unknown error. Please try again."
  }
}

export const errorUtil: {
  parseError: (error: any) => string
} = {
  parseError: parseError
}
