import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseSearch} from '../dto/horse';
import {Sex} from '../dto/sex';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }


  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  edit(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(
      baseUri + '/' + horse.id,
      horse
    );
  }

  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(
      baseUri + '/' + id);
  }

  delete(id: number){
    return this.http.delete<Horse>(
      baseUri + '/' + id
    );
  }

  public searchByName(name: string, limitTo: number, sex: Sex): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('limit', limitTo)
      .set('sex', sex);
    return this.http.get<Horse[]>(baseUri, { params });
  }

  public search(searchParams: HorseSearch){
    console.log('service:', searchParams.sex);
    const params = new HttpParams()
      .set('name', searchParams.name === undefined ? '' :  searchParams.name)
      .set('description', searchParams.description === undefined ? '' : searchParams.description)
      .set('bornBefore', searchParams.bornBefore === undefined ? '' : searchParams.bornBefore.toString())
      .set('sex', searchParams.sex === undefined ? '' : searchParams.sex)
      .set('ownerName', searchParams.owner === undefined ? '' : searchParams.owner);
    return this.http.get<Horse[]>(baseUri, {params});
  }

}
