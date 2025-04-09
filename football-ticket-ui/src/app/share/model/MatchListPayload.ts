export class MatchListPayload {
  constructor(
    public page: number = 1,
    public size: number = 10,
    public key: string = '',
    public date: string | null = '',
    public status: string[] = ['ALL']
  ) {
  }
}
