const regexp: RegExp = /(?<=^[htps]*:\/\/[a-zA-Z0-9\-]*\.[a-zA-Z]*\/).*/g;
const extract = (url: string) => {
  const path: RegExpMatchArray | null = regexp.exec(url);
  if (path == null || path.length === 0) {
    return null;
  }
  return path[0];
}

export const urlPath: {
  extract: (url: string) => string | null
} = {
  extract
}
