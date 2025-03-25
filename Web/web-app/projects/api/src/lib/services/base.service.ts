

export abstract class BaseService {

  protected abstract PATH: string;
  protected readonly DOMAIN: string;
  constructor(apiBaseUrl: string) {
    this.DOMAIN = apiBaseUrl ?? "https://fit.local/api/";
  }

  protected get path(): string {
    return this.DOMAIN + this.PATH;
  }
}
