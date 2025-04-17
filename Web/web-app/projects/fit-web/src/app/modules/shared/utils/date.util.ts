const getStart = (date: Date): string => {
  return `${date.getFullYear()}-${leadingZero(date.getMonth() + 1)}-01`
}
const getEnd = (date: Date): string => {
  const newDate: Date = new Date(date.getFullYear(), date.getMonth() + 1, 0);
  return `${newDate.getFullYear()}-${leadingZero(newDate.getMonth() + 1)}-${newDate.getDate()}`
}

const leadingZero = (digit: number): string => {
  if (digit < 10) {
    return `0${digit}`
  }
  return `${digit}`;
}
export const Dateutil: {
  getStart: (date: Date) => string,
  getEnd: (date: Date) => string
} = {
  getEnd: getEnd,
  getStart: getStart
}
