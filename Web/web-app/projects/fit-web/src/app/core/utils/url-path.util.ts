import {environment} from '../../../environments/environment';


const path: string = environment.production + '/main/users/login';
const extract = (url: string) => {
  if (url == null || url.length === 0) {
    return null;
  }
  return path[0];
}

export const urlPath: {
  extract: (url: string) => string | null
} = {
  extract
}
