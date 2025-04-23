import {environment} from '../../../environments/environment';

const regexp: RegExp = /(?<=^[htps]*:\/\/[a-zA-Z0-9\-]*\.[a-zA-Z]*\/).*/g;
const prodRegexp: RegExp = /(?<=^[htps]*:\/\/[a-zA-Z0-9\-]*\.[a-zA-Z0-9\-]*\.[a-zA-Z]*\/).*/g;
const currentRegexp: RegExp = environment.production ? prodRegexp : regexp;
const extract = (url: string) => {
  const path: RegExpMatchArray | null = currentRegexp.exec(url);
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
